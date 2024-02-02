package com.wrbread.roll.rollingpaper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {
    //첫 화면
    @GetMapping(value = {"", "/"})
    public String main() {
        return "main";
    }

}
