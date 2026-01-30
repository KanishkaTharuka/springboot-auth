package com.example.auth.dto;

import com.example.auth.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponseDTO {
    private String message;
    private UserModel userModel;
}
