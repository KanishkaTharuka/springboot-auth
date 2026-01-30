package com.example.auth.service;

import com.example.auth.dto.ResetPasswordRequestDTO;
import com.example.auth.exception.MissingEmailAndOtpException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.OTPModel;
import com.example.auth.model.UserModel;
import com.example.auth.repo.OTPRepo;
import com.example.auth.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PasswordResetService {

    @Autowired
    private OTPRepo otpRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String verifyOTP(ResetPasswordRequestDTO resetPasswordRequestDTO){
        String email = resetPasswordRequestDTO.getEmail();
        String otp = resetPasswordRequestDTO.getOtp();
        String newPassword = resetPasswordRequestDTO.getNewPassword();

        System.out.println(email);
        System.out.println(otp);
        System.out.println(newPassword);

        OTPModel otpModel = otpRepo.findByEmailAndOtp(email, otp)
                .orElseThrow(()->
                        new MissingEmailAndOtpException("invalid OTP"));

        UserModel user = userRepo.findByEmail(email)
                .orElseThrow(()->
                        new UserNotFoundException("User Not Found.....!"));

        String encodeedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodeedPassword);

        userRepo.save(user);

        otpRepo.deleteByEmail(email);

        return "password reset successfully....!";
    }

}
