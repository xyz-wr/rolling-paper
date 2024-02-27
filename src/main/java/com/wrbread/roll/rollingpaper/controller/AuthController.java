package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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
    public String join(@Valid @ModelAttribute AuthDto.JoinDto joinDto,
                       BindingResult bindingResult) {
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
    public String loginPage(Model model) {
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

        AuthDto.UserDto userDto = new AuthDto.UserDto(user);

        model.addAttribute("userDto", userDto);
        model.addAttribute("profileImgDto", userDto.getProfileImgDto());

        return "user/info";
    }

    /** 이용권 구매 페이지 */
    @GetMapping("/purchase/subscription")
    public String purchasePage(Model model) {
        User user = userService.verifiedEmail();
        model.addAttribute("isSubscriber", user.isSubscriber());

        return "user/purchase";
    }

    /** 이용권 구매 */
    @PostMapping("/purchase/subscription")
    public String purchase() {
        userService.purchaseSubscription();

        return "redirect:/papers/all-public-papers";
    }

    /** 유저 수정 페이지 */
    @GetMapping("/edit/user/{user-id}")
    public String update(@PathVariable("user-id") Long userId, Model model) {
        User user = userService.getUser(userId);
        AuthDto.UserDto userDto = new AuthDto.UserDto(user);

        model.addAttribute("userDto", userDto);
        model.addAttribute("profileImgDto", userDto.getProfileImgDto());
        model.addAttribute("profileImgId", userDto.getProfileImgId());
        return "user/edit";
    }

    /** 유저 수정 */
    @PostMapping("/edit/user/{user-id}")
    public String edit(@PathVariable("user-id") Long userId, @Valid AuthDto.UserDto userDto,
                       @RequestParam("profileImg") MultipartFile file, Model model) {
        try {
            userService.updateUser(userId, userDto, file);
        } catch (BusinessLogicException ex) {
            model.addAttribute("errorMessage", ex.getMessage());

            User user = userService.getUser(userId);
            model.addAttribute("userDto", new AuthDto.UserDto(user));
            model.addAttribute("profileImgDto", new AuthDto.UserDto(user).getProfileImgDto());
            return "user/edit";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "유저 정보 수정 중 에러가 발생하였습니다.");
            return "user/edit";
        }

        return "redirect:/auth/info";
    }

    /** 유저 탈퇴 페이지*/
    @GetMapping("/delete/user/{user-id}")
    public String delete(@PathVariable("user-id") Long userId, Model model) throws Exception{
        User user = userService.getUser(userId);
        AuthDto.UserDto userDto = new AuthDto.UserDto(user);

        model.addAttribute("userDto", userDto);

        return "user/delete";
    }

    /** 유저 탈퇴 */
    @PostMapping("/delete/user/{user-id}")
    public String withdraw(@PathVariable("user-id") Long userId, Model model) {
        try {
            userService.deleteUser(userId);
        } catch (BusinessLogicException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "user/delete";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "탈퇴 중 오류가 발생하였습니다.");
            return "user/delete";
        }

        return "redirect:/";
    }
}
