package com.capstone.eqh.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

// 일반 로그인 요청
@NoArgsConstructor
public class LoginResponseDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
