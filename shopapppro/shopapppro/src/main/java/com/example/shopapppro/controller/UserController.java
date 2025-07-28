package com.example.shopapppro.controller;


import com.example.shopapppro.dtos.ProductDTO;
import com.example.shopapppro.dtos.UserDTO;
import com.example.shopapppro.dtos.UserLoginDTO;
import com.example.shopapppro.models.User;
import com.example.shopapppro.service.user.IUserService;
import com.example.shopapppro.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private  final IUserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result){

        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return  ResponseEntity.badRequest().body("mật khẩu sai");
            }
            User user=userService.createUser(userDTO);
           // return ResponseEntity.ok("register successfully");
            return  ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO){
//        kiểm tra đăng nhập và tạo token
        try {
            String token = userService.login(userLoginDTO.getPhoneNumber(),userLoginDTO.getPassword());
            return  ResponseEntity.ok(token);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }
//        trả tk token vè response
    }
}
