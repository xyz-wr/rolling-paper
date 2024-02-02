package com.wrbread.roll.rollingpaper.auth.config;

import com.wrbread.roll.rollingpaper.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Bean
    public BCryptPasswordEncoder encoder() {
        //비밀번호를 DB에 저장하기 전 사용할 암호화
        return new BCryptPasswordEncoder();
    }

    //특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .csrf().disable() //csrf 비활성화
                .authorizeRequests() //인증, 인가 설정
                .requestMatchers("/auth/info", "/papers/**", "/messages/**").hasRole("USER")
                .anyRequest().permitAll()
                .and()
                .formLogin()//폼 기반 로그인 설정
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/auth/login")
                .defaultSuccessUrl("/papers/all-public-papers")
                .failureUrl("/auth/login")
                .and()
                .logout() //로그아웃 설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
//                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

