package com.example.auth.service;

import com.example.auth.config.JwtUtil;
import com.example.auth.dto.*;
import com.example.auth.exception.EmailAlreadyExistsException;
import com.example.auth.exception.MissingUserIdException;
import com.example.auth.exception.PasswordNotMatchException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.UserModel;
import com.example.auth.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.SocketHandler;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public List<UserModel> getAllUsers(FilterRequestDTO filterRequestDTO){

        Sort sort = Sort.unsorted();

        if("name".equals(filterRequestDTO.getSort())){
            sort = Sort.by("name").ascending();
        } else if ("city".equals(filterRequestDTO.getSort())) {
            sort = Sort.by("address").ascending();
        } else if ("date".equals(filterRequestDTO.getSort())) {
            sort = Sort.by("date").descending();
        }

        if("admin".equals(filterRequestDTO.getRole()) || "user".equals(filterRequestDTO.getRole())){
            return userRepo.findByRole(filterRequestDTO.getRole(), sort);
        }

        return userRepo.findAll(sort);
    }

    public UserModel getUserByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new MissingUserIdException("UserId is required");
        }
        UserModel user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("user Not Found"));

        return user;
    }

    public RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
        UserModel user = userRepo.findUserByEmail(registerRequestDTO.getEmail());

        if (user != null) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserModel lastUser = userRepo.findTopByOrderByDateDesc();

        String newUserId = "UID36001";
        String role = "USER";

        if(lastUser != null){
            String lastId = lastUser.getUserId();
            int number = Integer.parseInt(lastId.replace("UID", ""));
            newUserId = "UID"+(number + 1);
        }

        UserModel userModel = new UserModel();

        userModel.setUserId(newUserId);
        userModel.setEmail(registerRequestDTO.getEmail());
        userModel.setName(registerRequestDTO.getName());
        userModel.setRole(role);
        userModel.setAddress(registerRequestDTO.getAddress());
        userModel.setPhoneNumber(registerRequestDTO.getPhoneNumber());
//        password
        userModel.setPassword(
                passwordEncoder.encode(registerRequestDTO.getPassword())
        );

        UserModel saveUser = userRepo.save(userModel);

        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO(
                "Register Successful....!",
                saveUser
        );


        return registerResponseDTO;

    }

    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO)  {
        UserModel userModel = userRepo.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(()->
                        new UserNotFoundException("UserNot found.....!")
                );

        if(userModel == null){
            throw new UserNotFoundException("User Not Found...!");
        }

        boolean isPasswordMatch = passwordEncoder.matches(
                loginRequestDTO.getPassword(),
                userModel.getPassword()
        );

        if(!isPasswordMatch){
            throw new PasswordNotMatchException("password Not Match...!");
        }

        String token = jwtUtil.generateToken(
                userModel.getEmail(),
                userModel.getRole()
        );

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                "Login Successful.....!",
                token,
                userModel.getRole()
        );

        return loginResponseDTO;

    }

    public String deleteUserById(String userId) {

        if (userId == null || userId.trim().isEmpty()) {
            throw new MissingUserIdException("UserId is required");
        }

        UserModel user = userRepo.findByUserId(userId)
                        .orElseThrow(() -> new UserNotFoundException("user not found"));

        userRepo.delete(user);

        return "deleted user...!";
    }

    public UserModel updateUserById(String userId, UserModel updateUser){

        if (userId == null || userId.trim().isEmpty()) {
            throw new MissingUserIdException("UserId is required");
        }

        UserModel user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("user Not Found"));

        user.setName(updateUser.getName());
        user.setEmail(updateUser.getEmail());
        user.setAddress(updateUser.getAddress());
        user.setPhoneNumber(updateUser.getPhoneNumber());

        UserModel userModel = userRepo.save(user);

        return userModel;
    }

    public UserModel updateuserRole(String userId, String newRole) {
        UserModel userModel = userRepo.findByUserId(userId)
                .orElseThrow(()->
                        new UserNotFoundException("user not Found.....!"));

        userModel.setRole(newRole);
        return userRepo.save(userModel);
    }

}
