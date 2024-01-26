package com.wrbread.roll.rollingpaper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.service.AuthService;
import com.wrbread.roll.rollingpaper.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class AuthApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("회원가입 테스트")
    public void testSignup() throws Exception {
        //given
        AuthDto.SignupDto signupDto = new AuthDto.SignupDto();
        signupDto.setNickname("testNickname");
        signupDto.setEmail("test@gmail.com");
        signupDto.setPassword("testPassword");
        signupDto.setPasswordCheck("testPassword");
        signupDto.setId(1L);

        User user = signupDto.toEntity("ABCDEF", "testEncodePassword");

        given(userService.join(any(AuthDto.SignupDto.class))).willReturn(user);

        String content = objectMapper.writeValueAsString(signupDto);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/auth/join")
                .build()
                .toUri();


        //when
        ResultActions actions = mockMvc.perform(post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

        //then
        actions
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/auth/join")));
    }


    @Test
    @DisplayName("로그인 테스트")
    public void testLogin() throws Exception {
        //given
        AuthDto.LoginDto loginDto = new AuthDto.LoginDto();
        loginDto.setEmail("testEmail@gmail.com");
        loginDto.setPassword("testPassword");

        AuthDto.TokenDto tokenDto = new AuthDto.TokenDto();
        tokenDto.setAccessToken("testAccessToken");
        tokenDto.setRefreshToken("testRefreshToken");
        given(authService.login(any(AuthDto.LoginDto.class))).willReturn(tokenDto);

        String content = objectMapper.writeValueAsString(loginDto);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/auth/login")
                .build()
                .toUri();

        //when
        ResultActions actions = mockMvc.perform(post(uri)
                .header("Authorization", "Bearer testAccessToken")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken"));
    }

    @Test
    @DisplayName("refresh 토큰 재발행 테스트")
    public void testReissue() throws Exception {
        //given
        AuthDto.TokenDto reissuedTokenDto = new AuthDto.TokenDto("newAccessToken", "newRefreshToken");
        given(authService.reissue(any(String.class), any(String.class))).willReturn(reissuedTokenDto);

        //when
        ResultActions actions = mockMvc.perform(post("/api/auth/reissue")
                .header("Authorization", "Bearer validAccessToken")
                .cookie(new Cookie("refresh-token", "validRefreshToken"))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer newAccessToken"))
                .andExpect(cookie().value("refresh-token", "newRefreshToken"));
    }


    @Test
    @DisplayName("로그아웃 테스트")
    public void testLogout() throws Exception {
        ResultActions actions = mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer validAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
        );

        actions
                .andExpect(status().isOk())
                .andExpect(cookie().value("refresh-token", ""));
    }

    @Test
    @DisplayName("이메일 전송 테스트")
    public void testSendEmail() throws Exception {
        //given
        AuthDto.EmailDto email = new AuthDto.EmailDto();
        email.setEmail("test@gmail.com");

        String content = new ObjectMapper().writeValueAsString(email);

        //when
        ResultActions actions = mockMvc.perform(post("/api/auth/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        //then
        actions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증번호 확인 테스트")
    public void testCheckEmail() throws Exception {
        // Mock 데이터 설정
        AuthDto.EmailCheckDto emailCheck = new AuthDto.EmailCheckDto();
        emailCheck.setEmail("test@example.com");
        emailCheck.setAuthKey("valid_auth_key");

        given(authService.checkAuthEmail(any(String.class), any(String.class))).willReturn(Boolean.TRUE);

        String content = new ObjectMapper().writeValueAsString(emailCheck);

        //when
        ResultActions actions = mockMvc.perform(post("/api/auth/checkEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(content().string("인증되었습니다."));
    }
}