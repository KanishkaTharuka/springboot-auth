package com.example.auth.repo;

import com.example.auth.model.OTPModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OTPRepo extends JpaRepository<OTPModel, Long> {
    void deleteByEmail(String email);

    Optional<OTPModel> findByEmailAndOtp(String email, String otp);
}
