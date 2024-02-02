package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.auth.JwtTokenProvider;
import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final JavaMailSender javaMailSender;
    private final RandomUtil randomUtil;

    private final UserService userService;

    @Value("${spring.mail.username}")
    private String configEmail;

    /** 로그인: 인증 정보 저장 및 비어 토큰 발급 */
    @Transactional
    public AuthDto.TokenDto login(AuthDto.LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return generateToken(authentication.getName(), getAuthorities(authentication));
    }

    /** AccessToken가 만료일자만 초과한 유효한 토큰인지 검사 */
    public boolean validate(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        return jwtTokenProvider.validateAccessTokenOnlyExpired(requestAccessToken); // true = 재발급
    }

    /** 토큰 재발급: validate 메서드가 true 반환할 때만 사용 -> Access-token, Refresh-token 재발급 */
    @Transactional
    public AuthDto.TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);
        String principal = getPrincipal(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT:" + principal);


        if (refreshTokenInRedis == null) { // Redis에 저장되어 있는 RT가 없을 경우
            return null; // -> 재로그인 요청
        }
        // 요청된 Refresh-token의 유효성 검사 & Redis에 저장되어 있는 Refresh-token와 같은지 비교
        if(!jwtTokenProvider.validateRefreshToken(requestRefreshToken) || !refreshTokenInRedis.equals(requestRefreshToken)) {
            redisService.deleteValues("RT:" + principal); // 탈취 가능성 -> 삭제
            return null; // -> 재로그인 요청
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        // 토큰 재발급 및 Redis 업데이트
        redisService.deleteValues("RT:" + principal); // 기존 Refresh-token 삭제
        AuthDto.TokenDto tokenDto = jwtTokenProvider.createToken(principal, authorities);
        saveRefreshToken(principal, tokenDto.getRefreshToken());
        return tokenDto;
    }


    /** 토큰 발급 */
    @Transactional
    public AuthDto.TokenDto generateToken(String email, String authorities) {
        // Refresh-token가 이미 있을 경우
        if(redisService.getValues("RT:" + email) != null) {
            redisService.deleteValues("RT:" + email); // 삭제
        }

        // Access-token, Refresh-token 생성 및 Redis에 RT 저장
        AuthDto.TokenDto tokenDto = jwtTokenProvider.createToken(email, authorities);
        saveRefreshToken(email, tokenDto.getRefreshToken());
        return tokenDto;
    }

    /** Refresh-token를 Redis에 저장 */
    @Transactional
    public void saveRefreshToken(String principal, String refreshToken) {
        redisService.setValuesWithTimeout("RT:" + principal, // key
                refreshToken, // value
                jwtTokenProvider.getTokenExpirationTime(refreshToken)); // timeout(milliseconds)
    }

    // 권한 이름 가져오기
    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    /** Access-token으로부터 principal 추출 */
    public String getPrincipal(String requestAccessToken) {
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }

    /** "Bearer {Access-token}"에서 {Access-token} 추출 */
    public String resolveToken(String requestAccessTokenInHeader) {
        if (requestAccessTokenInHeader != null && requestAccessTokenInHeader.startsWith("Bearer ")) {
            return requestAccessTokenInHeader.substring(7);
        }
        return null;
    }

    /** 로그아웃 */
    @Transactional
    public void logout(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        String principal = getPrincipal(requestAccessToken);

        // Redis에 저장되어 있는 Refresh-token 삭제
        String refreshTokenInRedis = redisService.getValues("RT:" + principal);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues("RT:" + principal);
        }

        // Redis에 로그아웃 처리한 AccessToken 저장
        long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
        redisService.setValuesWithTimeout(requestAccessToken,
                "logout",
                expiration);
    }

    /** 이메일 인증 */
    @Transactional
    public void sendAuthEmail(String email) {
        if (userService.existsEmail(email)) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_EXISTS);
        }

        String authKey = randomUtil.generateRandomString();

        String subject = "Rolling Paper 회원가입 인증 번호입니다.";
        String content = "Rolling Paper 회원가입 인증 번호는 " + authKey + " 입니다.";

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(configEmail);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("이메일 전송 중 오류가 발생하였습니다.");
        }

        redisService.setValuesWithTimeout(authKey, email, 60 * 5L);
    }

    /** 이메일 인증번호 확인 */
    public boolean checkAuthKey(String email, String authKey) {
        if (redisService.getValues(authKey) == null) {
            return false;
        } else if (redisService.getValues(authKey).equals(email)) {
            return true;
        } else {
            return false;
        }
    }
}
