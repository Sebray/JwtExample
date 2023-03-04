package com.sebray.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class RoleController {

    @GetMapping("/user")
    public ResponseEntity<?> testUser() {
        return ResponseEntity.ok()
                .body("User");
    }

    @GetMapping("/admin")
    public ResponseEntity<?> testAdmin() {
        return ResponseEntity.ok()
                .body("Admin");
    }

    @GetMapping("/all")
    public ResponseEntity<?> testAll() {
        return ResponseEntity.ok()
                .body("All");
    }
}
