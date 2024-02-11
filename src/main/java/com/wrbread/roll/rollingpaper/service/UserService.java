package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.ProfileImg;
import com.wrbread.roll.rollingpaper.model.entity.User;
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
@Transactional(readOnly = true)
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RandomUtil randomUtil;

    private final ProfileImgRepository profileImgRepository;

    private final ProfileImgService profileImgService;


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
    @Transactional
    public void purchaseSubscription() {
        User user = verifiedEmail();
        user.purchaseSubscription();
        userRepository.save(user);
    }

    /** 유저 수정
     * 닉네임, 프로필 사진 변경
     * */
    @Transactional
    public User updateUser(Long id, AuthDto.UserDto userDto,
                           MultipartFile file) throws Exception{

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        String nickname = user.getNickname();

        if (userDto.getNickname() == null) {
            userDto.setNickname(nickname);
        }


        String imgUrl = user.getProfileImg().getImgUrl();
        profileImgService.deleteProfileImg(imgUrl);

        profileImgService.saveProfileImg(user, file);
        user.updateUser(userDto.getNickname());

        return userRepository.save(user);
    }
}
