package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.model.entity.ProfileImg;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.repository.InvitationRepository;
import com.wrbread.roll.rollingpaper.repository.ProfileImgRepository;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import com.wrbread.roll.rollingpaper.util.RandomUtil;
import com.wrbread.roll.rollingpaper.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RandomUtil randomUtil;

    private final ProfileImgRepository profileImgRepository;

    private final ProfileImgService profileImgService;

    private final InvitationRepository invitationRepository;

    private final String DEFAULT_PROFILE_IMG = "https://rolling-paper.s3.ap-northeast-2.amazonaws.com/default/default_profileImg.png";

    @Transactional
    public User join(AuthDto.JoinDto joinDto) {
        if (checkNickname(joinDto.getNickname())) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_EXISTS);
        }

        if (checkEmail(joinDto.getEmail())) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_EXISTS);
        }

        if (!joinDto.getPassword().equals(joinDto.getPasswordCheck())) {
            throw new BusinessLogicException(ExceptionCode.PASSWORD_NOT_MATCH);
        }

        User user = joinDto.toEntity(randomUtil.generateRandomString(), passwordEncoder.encode(joinDto.getPassword()));

        ProfileImg profileImg = ProfileImg
                .builder()
                .user(user)
                .imgUrl(DEFAULT_PROFILE_IMG)
                .build();

        profileImg.addUser(user);

        profileImgRepository.save(profileImg);

        return userRepository.save(user);
    }

    /** 이메일 중복 체크
     * 회원가입 기능 구현 시 사용
     */
    public boolean checkEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    /** nickname 중복 체크
     * 회원가입 기능 구현 시 사용
     */
    public boolean checkNickname(String nickname) {

        return userRepository.existsByNickname(nickname);
    }

    /** 유저 이메일 조회 */
    public User verifiedEmail() {
        String email = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        return user;
    }

    /** 특정 유저 이메일 조회 */
    public boolean existsEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    /** 유저 검색
     * 코드네임으로 검색
     * */
    public User findCodename(String codename) {
        return userRepository.findByCodename(codename);
    }

    /** 이용권 구매 */
    public void purchaseSubscription() {
        User user = verifiedEmail();
        user.purchaseSubscription();
        userRepository.save(user);
    }

    /** 유저 수정
     * 닉네임, 프로필 사진 변경
     * */
    public User updateUser(Long id, AuthDto.UserDto userDto,
                           MultipartFile file) throws Exception{

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        // 닉네임을 변경하지 않는 경우 기존 닉네임 유지
        String nickname = user.getNickname();
        if (userDto.getNickname() == null) {
            userDto.setNickname(nickname);
        }

        // 같은 닉네임이 존재하는 경우 예외 처리
        if (checkEditNickname(userDto.getNickname(), id)) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_EXISTS);
        }

        // 프로필 이미지 변경
        Long imgId = user.getProfileImg().getId();
        profileImgService.deleteProfileImg(imgId);
        ProfileImg profileImg = profileImgService.saveProfileImg(user, file);

        user.updateUser(userDto.getNickname(), profileImg);

        return userRepository.save(user);
    }

    /** 유저 조회
     * 해당 유저만 접근 가능*/
    public User getUser(Long id) {

        User currentUser = verifiedEmail();

        User updateUser = userRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        // 인증된 사용자와 수정 대상 유저의 일치 여부 확인
        if (!currentUser.getId().equals(updateUser.getId())) {
            throw new BusinessLogicException(ExceptionCode.NO_PERMISSION_ACCESS);
        }

        return updateUser;
    }

    /** nickname 중복 체크
     * 자기 자신 제외
     * */
    public boolean checkEditNickname(String nickname, Long userId) {
        List<User> users = userRepository.findAll();

        return users.stream()
                .anyMatch(user -> !user.getId().equals(userId) && user.getNickname().equals(nickname));

    }

    /** 유저 삭제
     * 삭제 시 s3에 있는 파일 제거
     * 관련 초대장 제거
     * */
    public void deleteUser(Long id) throws Exception {
        User user = getUser(id);

        // 프로필 사진 제거
        Long imgId = user.getProfileImg().getId();
        profileImgService.deleteProfileImg(imgId);

        // 발신자로서의 초대장 모두 삭제
        List<Invitation> sentInvitations = invitationRepository.findBySender(user);
        invitationRepository.deleteAll(sentInvitations);

        // 수신자로서의 초대장 모두 삭제
        List<Invitation> receivedInvitations = invitationRepository.findByReceiver(user);
        invitationRepository.deleteAll(receivedInvitations);

        userRepository.delete(user);
    }
}
