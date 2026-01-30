package com.example.auth.repo;

import com.example.auth.model.UserModel;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);

    UserModel findUserByEmail(String email);

    UserModel findTopByOrderByDateDesc();

    Optional<UserModel> findByUserId(String userId);

    List<UserModel> findByRole(String role, Sort sort);


}
