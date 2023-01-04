package com.sebray.jwt.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoDto {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}
