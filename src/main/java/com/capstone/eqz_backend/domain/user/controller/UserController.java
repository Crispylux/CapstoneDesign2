package com.capstone.eqz_backend.domain.user.controller;

import com.capstone.eqz_backend.domain.user.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class UserController {
    //개인정보 조회
    @GetMapping("/api/users/me")
    public String getProfile() {

    }
}
