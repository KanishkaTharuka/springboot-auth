package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.model.UserModel;
import com.example.auth.service.OTPService;
import com.example.auth.service.PasswordResetService;
import com.example.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(value="/api/auth/users")

public class AuthController {

    private final UserService userService;

    private final OTPService otpService;

    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService, OTPService otpService, PasswordResetService passwordResetService) {

        this.userService = userService;
        this.otpService = otpService;
        this.passwordResetService = passwordResetService;
    }

//    getAllUsers(filter and sortObject (admin side)) "/  ok
//    createUser "/register"  ok
//    loginUser "/login"  ok
//    getUserById (user side) "/:id"  ok
//    updateUserById (can user) "/:id"  ok
//    deleteUserById (can user) "/:id"  ok
//    sendOTP "/send-otp"  ok
//    resetPassword "/verify-otp"  ok
//    updateUserRole(can admin) "/update-role/:id"  ok


    @PostMapping("/register")
    public RegisterResponseDTO registerUser(@RequestBody RegisterRequestDTO registerRequestDTO){
        return userService.registerUser(registerRequestDTO);
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody LoginRequestDTO loginRequestDTO) throws Exception {
        return userService.loginUser(loginRequestDTO);
    }

    @GetMapping//get All Users(admin)
    @PreAuthorize("hasRole('ADMIN')")  // SecurityConfig file connect in @PreAuthorize
    public List<UserModel> getAllusers(@RequestParam(required = false) String sort,
                                       @RequestParam(required = false) String role){
        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setRole(role);
        filterRequestDTO.setSort(sort);
        return userService.getAllUsers(filterRequestDTO);
    }

    @GetMapping("/{userId}")  // get user by id(user)
    @PreAuthorize("hasRole('USER')")
    public UserModel getUserByUserId(@PathVariable String userId) {
        return userService.getUserByUserId(userId);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUserById(@PathVariable String userId){
        return userService.deleteUserById(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public UserModel updateUserById(@PathVariable String userId, @RequestBody UserModel updateUser){
        return userService.updateUserById(userId, updateUser);
    }

    @PutMapping("/update_role/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(

            @PathVariable String userId,
            @RequestBody UpdateRoleRequestDTO updateRoleRequestDTO
    ){
        System.out.println("role update");
        UserModel userModel = userService.updateuserRole(userId, updateRoleRequestDTO.getRole());
        return ResponseEntity.ok(
                Map.of(
                        "message", "succesful role updated",
                        "user", userModel
                )
        );
    }

    @PostMapping("/send_otp")
    public ResponseEntity<?> sendOTP(@RequestBody SendOtpDTO sendOtpDTO){

        String email = sendOtpDTO.getEmail();
        String message = otpService.sendOTP(email);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/verify_otp")
    public ResponseEntity<?> verifyOTP(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO){
        System.out.println("verify password");
        String response = passwordResetService.verifyOTP(resetPasswordRequestDTO);
        return ResponseEntity.ok(response);
    }


}
