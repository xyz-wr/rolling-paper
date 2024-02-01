package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.service.AuthService;
import com.wrbread.roll.rollingpaper.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;


    /** 회원가입 페이지 로딩 */
    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("joinDto", new AuthDto.JoinDto());
        return "user/join";
    }

    /** 회원가입 */
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute AuthDto.JoinDto joinDto, BindingResult bindingResult,
                       Model model) {
        //nickname 중복 체크
        if (userService.checkNickname(joinDto.getNickname())) {
            bindingResult.addError(new FieldError("joinDto", "nickname", "중복된 닉네임입니다."));
        }

        //email 중복 체크
        if (userService.checkEmail(joinDto.getEmail())) {
            bindingResult.addError(new FieldError("joinDto", "email", "중복된 이메일입니다."));
        }

        //password와 checkPassword가 일치하는지 확인
        if (!joinDto.getPassword().equals(joinDto.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinDto", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if (bindingResult.hasErrors()) {
            return "user/join";
        }

        userService.join(joinDto);
        return "redirect:/auth/login";
    }

    /** 로그인 */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDto", new AuthDto.LoginDto());
        return "user/login";
    }

    /** 회원 정보 */
    @GetMapping("/info")
    public String userInfo(Model model) {
        User user = userService.verifiedEmail();

        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        return "user/info";
    }

}
