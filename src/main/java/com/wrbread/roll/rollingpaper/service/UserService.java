package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import com.wrbread.roll.rollingpaper.util.RandomUtil;
import com.wrbread.roll.rollingpaper.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RandomUtil randomUtil;

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
    public User findCodename(AuthDto.UserDto userDto) {
        String codename = userDto.getCodename();
        User user = userRepository.findByCodename(codename)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CODENAME_NOT_FOUND));
        return user;
    }
}
