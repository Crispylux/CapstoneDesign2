package com.capstone.eqh.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SignupRequestDto {
    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

    @NotBlank
    @Email String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8~20자, 영문+숫자+특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank
    private String passwordConfirm;

}
