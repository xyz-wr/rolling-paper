package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import com.wrbread.roll.rollingpaper.util.RandomUtil;
import com.wrbread.roll.rollingpaper.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RandomUtil randomUtil;

    @Transactional
    public User join(AuthDto.SignupDto signupDto) {
        if (checkNickname(signupDto.getNickname())) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_EXISTS);
        }

        if (checkEmail(signupDto.getEmail())) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_EXISTS);
        }

        if (!signupDto.getPassword().equals(signupDto.getPasswordCheck())) {
            throw new BusinessLogicException(ExceptionCode.PASSWORD_NOT_MATCH);
        }

        User user = signupDto.toEntity(randomUtil.generateRandomString(), passwordEncoder.encode(signupDto.getPassword()));
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
}
