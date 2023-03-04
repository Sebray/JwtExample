package com.sebray.jwt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    private String role;
    private String activationCode;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
