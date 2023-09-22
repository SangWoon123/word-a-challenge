package com.example.demo.dto;

import lombok.Getter;

@Getter
public class Token {
    String token;
    String refreshToken;

    public Token(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
