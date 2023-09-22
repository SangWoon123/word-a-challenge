package com.example.demo.service;

import com.example.demo.model.UserEntity;
import com.example.demo.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public UserEntity create(final UserEntity userEntity){  // 개발 안정성을 위해 final 선언
        if(userEntity==null || userEntity.getUsername()==null){
            throw new RuntimeException("Invalid arguments");
        }

        final String username=userEntity.getUsername(); // 개발 안정성을 위해 final 선언

        if(userRepository.existsByUsername(username)){
            log.warn("Username already exists {}",username);
            throw new RuntimeException("Username already exists");
        }

        return userRepository.save(userEntity);
    }
    // 로그인시 인증에 사용되는 메서드
    public UserEntity getByCredentials(final String username,final String password,final PasswordEncoder encoder){
        final UserEntity originalUser=userRepository.findByUsername(username);

        //matches 메서드를 이용해 패스워드가 같은지 확인
        if(originalUser!=null && encoder.matches(password,originalUser.getPassword())) return originalUser;
        return null;
    }

    // 회원 탈퇴
    public void deleteUser(String userId) {
        UserEntity userEntity=validateVerifyMember(userId);
        userEntity.setMemberStatus(UserEntity.MemberStatus.QUIT);
        userRepository.save(userEntity);
    }

    // 유저가 존재하는지 확인하는 메서드
    private UserEntity validateVerifyMember(String userId){
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        return optionalUser.orElseThrow(() -> new RuntimeException("No exist User"));
    }

    // 1일이후 탈퇴로직
    private void delete(){
        userRepository.deleteAll(getQuitUsersAfterOneDay());
    }

    // 삭제되어야하는 유저 리스트 목록
    @Scheduled() // 1일후로 일단 생각
    private List<UserEntity> getQuitUsersAfterOneDay(){
        List<UserEntity> users=userRepository.findByMemberStatus(UserEntity.MemberStatus.QUIT);
        return users.stream()
                .filter(user->ChronoUnit.DAYS.between(user.getModifiedAt(), LocalDateTime.now()) >= 1)
                .collect(Collectors.toList());
    }

    // 휴면상 태이던 회원 활성화
    public UserEntity updateUser(String userId) {
        UserEntity userEntity =userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("회원이 존재하지 않습니다."));
        userEntity.setMemberStatus(UserEntity.MemberStatus.ACTIVE);
        log.info("{} 님의 휴면상태가 해제되었습니다.",userEntity.getUsername());

        return userRepository.save(userEntity);
    }

    // 아이디 중복 확인

}
