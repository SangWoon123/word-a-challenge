package com.example.demo.dto;

import com.example.demo.model.UserEntity;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String token;

    @NotBlank(message = "아이디는 필수 입력 값 입니다.")
    @Pattern(regexp = "[a-zA-Z0-9]{2,9}",
    message = "아이디는 영문 숫자만 가능하며 2~10자리까지 가능")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    @Pattern(regexp ="^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[~!@#$%^&*])[\\da-zA-Z~!@#$%^&*]+$",
            message = "영문자와 숫자로 구성되며 최소 하나 이상의 특수문자(~!@#$%^&*)가 포함되어야합니다. "+
                    "공백은 포함될 수 없습니다")
    @Size(min = 8,max = 16)
    private String password;
    private String id;
    //private String refreshToken;

}
