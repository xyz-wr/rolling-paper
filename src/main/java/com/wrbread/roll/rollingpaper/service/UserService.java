package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import com.wrbread.roll.rollingpaper.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

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
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        if (checkEmail(signupDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (!signupDto.getPassword().equals(signupDto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
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

}
