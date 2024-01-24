package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
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

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    @Transactional
    public void join(AuthDto.SignupDto signupDto) {
        if (checkNickname(signupDto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        if (checkEmail(signupDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (!signupDto.getPassword().equals(signupDto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User user = signupDto.toEntity(generateRandomString(), passwordEncoder.encode(signupDto.getPassword()));
        userRepository.save(user);
    }

    /** codename 랜덤 생성*/
    public static String generateRandomString() {
        int length = 6; // 원하는 문자열 길이
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
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
