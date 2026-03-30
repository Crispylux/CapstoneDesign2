package com.capstone.eqz_backend.domain.user.entity;

import com.capstone.eqz_backend.domain.user.enums.AuthProvider;
import com.capstone.eqz_backend.global.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// 사용자 정의 권한 사용을 위한 Enum 파일 생성 후 import

@Entity
@Table(name = "users")
@SuppressWarnings({"LombokGetterMayBeUsed"})
public class User {
    //로컬 아이디와 소셜api 회원번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    //소셜은 null
    @Column(nullable = true)
    private String password;
    
    @Column(nullable = false)
    private String email;

    //LOCAL, KAKAO
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    //PROF,USER, ADMIN
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;



    //JPA가 Entity를 DataBase에 저장하기 전 호출하여 수정시간 설정
    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onModified() {
        this.modifiedAt = LocalDateTime.now();
    }
    //기본 생성자
    protected User() {}

    //getter
    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
//    public String getPassword() {
//        return password
//    }
    public String getEmail() {
        return email;
    }
    public Role getRole() {
        return role;
    }
    public AuthProvider getProvider() {
        return provider;
    }
    public LocalDateTime getCreateAt() {
        return createAt;
    }
    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
}