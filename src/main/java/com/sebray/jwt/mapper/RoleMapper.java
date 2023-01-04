package com.sebray.jwt.mapper;

import com.sebray.jwt.entity.ERole;
import com.sebray.jwt.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public ERole toERole(String role){
        return switch (role) {
            case "ROLE_USER" -> ERole.ROLE_USER;
            case "ROLE_COMPANY" -> ERole.ROLE_COMPANY;
            case "ROLE_ADMIN" -> ERole.ROLE_ADMIN;
            default -> throw new ResourceNotFoundException("Role does not exist");
        };
    }
}
