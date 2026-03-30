package com.capstone.eqh.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
    //개인정보 조회
    @GetMapping("/api/users/me")
    public String getProfile() {

    }
}
