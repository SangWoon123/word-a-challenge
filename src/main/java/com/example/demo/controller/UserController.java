package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.Token;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.UserEntity;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO){
        try {
            if (userDTO.getUsername() == null || userDTO.getPassword() == null) {
                throw new RuntimeException("Invalid Password value.");
            }

            UserEntity user = UserEntity.builder()
                    .username(userDTO.getUsername())
                    .memberStatus(UserEntity.MemberStatus.ACTIVE)
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build();

            UserEntity registeredUser = userService.create(user);
            UserDTO responseUserDTO = UserDTO.builder()
                    .id(registeredUser.getId())
                    .username(registeredUser.getUsername())
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        }catch (Exception e){
            ResponseDTO responseDTO=ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        UserEntity user=userService.getByCredentials(userDTO.getUsername(), userDTO.getPassword(),passwordEncoder);

        if(user!=null){
            //토큰 생성
            final String token=tokenProvider.create(user);

            final UserDTO responseUserDTO=UserDTO.builder()
                    .username(user.getUsername())
                    .id(user.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        }else{
            ResponseDTO responseDTO=ResponseDTO.builder()
                    .error("Login failed.")
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 회원 탈퇴 처리 (유효기간 1일로 1일이 지나면 db에서 완전히 삭제)
    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id")String userId){
        userService.deleteUser(userId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    // 휴면회원 살리기
    @PatchMapping("/{id}")
    public ResponseEntity patchUser(@PathVariable("id")String userId){
        return new ResponseEntity<>(userService.updateUser(userId),HttpStatus.OK);
    }
}
