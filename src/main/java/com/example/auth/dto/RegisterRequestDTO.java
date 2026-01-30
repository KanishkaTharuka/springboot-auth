package com.example.auth.dto;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class RegisterRequestDTO {
    private String name;
    private String email;
    private String password;
    private String role;
    private String phoneNumber;
    private String address;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
