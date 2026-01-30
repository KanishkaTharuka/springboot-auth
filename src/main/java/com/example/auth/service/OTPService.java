package com.example.auth.service;

import com.example.auth.exception.EmailNotFoundException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.OTPModel;
import com.example.auth.model.UserModel;
import com.example.auth.repo.OTPRepo;
import com.example.auth.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
public class OTPService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private OTPRepo otpRepo;

    @Autowired
    private UserRepo userRepo;

    public String sendOTP(String email){
        if(email == null || email.isEmpty()){
            throw new EmailNotFoundException("Email not found....!");
        }

        UserModel user = userRepo.findByEmail(email).orElseThrow(()->
                 new UserNotFoundException("User not Found....!")
        );

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        otpRepo.deleteByEmail(email);

        OTPModel newOtp = new OTPModel();

        newOtp.setOtp(otp);
        newOtp.setEmail(email);

        otpRepo.save(newOtp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kani.company.mail@gmail.com");
        message.setTo(email);
        message.setSubject("password Reset OTP");
        message.setText("your otp for password reset is "+otp);

        javaMailSender.send(message);

        return "OTP send Successfully...!";
    }


}
