//package com.wrbread.roll.rollingpaper.auth.config;
//
//import com.wrbread.roll.rollingpaper.auth.jwt.JwtAccessDeniedHandler;
//import com.wrbread.roll.rollingpaper.auth.jwt.JwtAuthenticationEntryPoint;
//import com.wrbread.roll.rollingpaper.auth.jwt.JwtTokenFilter;
//import com.wrbread.roll.rollingpaper.auth.jwt.JwtTokenProvider;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//    private final JwtTokenProvider jwtTokenProvider;
//
//    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//    @Bean
//    public BCryptPasswordEncoder encoder() {
//        //비밀번호를 DB에 저장하기 전 사용할 암호화
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        // ACL(Access Control List, 접근 제어 목록)의 예외 URL 설정
//        return (web)
//                -> web
//                .ignoring()
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적 리소스들
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        //인터셉터로 요청을 안전하게 보호하는 방법 설정
//        http
//                // jwt 토큰 사용을 위한 설정
//                .csrf(AbstractHttpConfigurer::disable)
//                .httpBasic(withDefaults())
//                .formLogin(withDefaults())
//                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//
//                // 예외 처리
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .accessDeniedHandler(jwtAccessDeniedHandler)
//
//                .and()
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/auth/test", "/api/papers/**", "/api/invitations/**",
//                                "/api/messages/**", "/api/auth/purchase/subscription",
//                                "api/auth/user/**").hasRole("USER")
//                        .anyRequest().permitAll())
//                .headers(headersConfigurer ->
//                        headersConfigurer
//                                .frameOptions(
//                                        HeadersConfigurer.FrameOptionsConfig::sameOrigin));
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//}
