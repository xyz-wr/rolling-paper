package com.wrbread.roll.rollingpaper.controller.Api;

import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.service.AuthService;
import com.wrbread.roll.rollingpaper.service.UserService;
import com.wrbread.roll.rollingpaper.util.SecurityUtil;
import com.wrbread.roll.rollingpaper.util.UriCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;
    private final UserService userService;
    private final long COOKIE_EXPIRATION = 7776000; // 90일

    /** 회원가입 */
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody @Valid AuthDto.JoinDto joinDto) {
        User user = userService.join(joinDto);

        URI location = UriCreator.createUri("/api/auth/join", user.getId());

        return ResponseEntity.created(location).build();
    }

    /** 로그인 -> 토큰 발급 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDto.LoginDto loginDto) {
        // User 등록 및 Refresh Token 저장
        AuthDto.TokenDto tokenDto = authService.login(loginDto);

        // Refresh-token 저장
        HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(true)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                // Access-token 저장
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken())
                .build();
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.OK).build(); // 재발급 필요X
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 재발급 필요
        }
    }

    /** 토큰 재발급 */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue("refresh-token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {
        AuthDto.TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);
        if (reissuedTokenDto != null) { // 토큰 재발급 성공
            // Refresh-token 저장
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .build();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    // Access-token 저장
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken())
                    .build();

        } else { // Refresh Token 탈취 가능성
            // Cookie 삭제 후 재로그인 유도
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        }
    }

    /** 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String requestAccessToken) {
        authService.logout(requestAccessToken);
        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @PostMapping("/test")
    public String test() {
        return SecurityUtil.getCurrentUsername();
    }

    /** 이메일 전송 */
    @PostMapping("/send/auth-key")
    public ResponseEntity<Void> sendEmail(@RequestParam("email") String email) {
        authService.sendAuthEmail(email);
        return ResponseEntity.ok().build();
    }

    /** 이메일 인증번호 확인 */
    @PostMapping("/check/auth-key")
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email,
                             @RequestParam("auth-key") String authKey) {
        boolean check = authService.checkAuthKey(email, authKey);
        if (check) {
            return ResponseEntity.ok("인증되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 인증번호입니다.");
        }
    }

    /** 이용권 구매 */
    @PostMapping("/purchase/subscription")
    public ResponseEntity<String> purchaseSubscription() {
        userService.purchaseSubscription();
        return ResponseEntity.ok("Subscription purchased successfully.");
    }

    /** 유저 수정 */
    @PatchMapping("/user/{user-id}")
    public ResponseEntity<AuthDto.UserDto> patchUser(@PathVariable("user-id") Long userId,
                                                     @Valid @RequestPart AuthDto.UserDto userDto,
                                                     @RequestPart(value = "profileImg", required = false) MultipartFile file) throws Exception {
        User user = userService.updateUser(userId, userDto, file);

        return ResponseEntity.ok().body(new AuthDto.UserDto(user));
    }

    /** 유저 삭제 */
    @DeleteMapping("/user/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") Long userId) throws Exception{
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    /** 유저 조회 */
    @GetMapping("/user/{user-id}")
    public ResponseEntity<AuthDto.UserDto> getUser(@PathVariable("user-id") Long userId) throws Exception{
        User user = userService.getUser(userId);

        return ResponseEntity.ok().body(new AuthDto.UserDto(user));
    }
}