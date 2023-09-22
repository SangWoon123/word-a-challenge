package com.example.demo.model;

import com.example.demo.audit.Auditable;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class UserEntity extends Auditable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid",strategy = "uuid")
    private String id;

    @Column(nullable = false)
    private String username;

    private String password;

    private String role;

    private String authProvider; // github,naver 등 oauth 에서 유저 정보 제공자

    @Enumerated(value = EnumType.STRING)
    private MemberStatus memberStatus=MemberStatus.ACTIVE; // 회원 탈퇴시 유효기간/ 휴면 설정

    private String refreshToken; // 리프레시 토큰

    public enum MemberStatus{
        ACTIVE("활성화"),
        SLEEP("휴면"),
        QUIT("탈퇴");

        @Getter
        private String memberStatus;

        MemberStatus(String memberStatus){
            this.memberStatus=memberStatus;
        }
    }



}
