package com.capstone.eqz_backend.domain.user.controller;

import com.capstone.eqz_backend.domain.user.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserLoginService userLoginService;

    @GetMapping({"/user/login", "/"})
    public String loginPage(Model model) {
        System.out.println("loginPage");
        return "api/auth/login";
    }
}
