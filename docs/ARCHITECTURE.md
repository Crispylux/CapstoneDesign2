# ARCHITECTURE — EQZ Backend 패키지 구조

> **패키지 루트**: `com.capstone.eqz_backend`

---

## 전체 구조

```
src/main/java/com/capstone/eqz_backend/
│
├── EqzBackendApplication.java
│
├── domain/
│   ├── user/
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   └── RefreshToken.java
│   │   ├── enums/
│   │   │   ├── Role.java                       # PROF / USER / ADMIN
│   │   │   └── AuthProvider.java               # LOCAL / KAKAO
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── RefreshTokenRepository.java
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── SignupRequest.java
│   │   │   │   ├── UserUpdateRequest.java
│   │   │   │   └── PasswordChangeRequest.java
│   │   │   └── response/
│   │   │       ├── AuthResponse.java           # accessToken 포함
│   │   │       └── UserResponse.java
│   │   ├── service/
│   │   │   ├── UserAuthService.java            # 로그인, 토큰 재발급, 로그아웃
│   │   │   ├── UserSignupService.java          # 회원가입
│   │   │   └── UserService.java               # 프로필 조회, 수정, 탈퇴
│   │   └── controller/
│   │       ├── AuthController.java            # /api/auth/**
│   │       └── UserController.java            # /api/users/**
│   │
│   ├── quiz/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   └── response/
│   │   ├── service/
│   │   └── controller/                        # /api/quiz/**
│   │
│   └── lesson/
│       ├── entity/
│       ├── repository/
│       ├── dto/
│       │   ├── request/
│       │   └── response/
│       ├── service/
│       └── controller/                        # /api/lesson/**
│
└── global/
    ├── jwt/
    │   ├── JwtProvider.java                   # 토큰 생성, 검증, 파싱
    │   └── JwtFilter.java                     # 모든 요청 JWT 검사
    │
    ├── oauth2/
    │   ├── info/
    │   │   ├── OAuth2UserInfo.java             # 추상 클래스
    │   │   └── KakaoOAuth2UserInfo.java
    │   ├── service/
    │   │   └── CustomOAuth2UserService.java    # 소셜 로그인 유저 저장/업데이트
    │   └── handler/
    │       └── OAuth2SuccessHandler.java       # JWT 발급 및 리다이렉트
    │
    ├── security/
    │   ├── SecurityConfig.java                # FilterChain 및 RBAC
    │   ├── CustomUserDetails.java
    │   └── CustomUserDetailsService.java
    │
    ├── exception/
    │   ├── ErrorCode.java
    │   ├── CustomException.java
    │   └── GlobalExceptionHandler.java
    │
    └── common/
        └── ApiResponse.java                   # 공통 응답 규격
```

---

## 도메인 책임 요약

| 도메인 | 경로 | 주요 역할 |
|--------|------|-----------|
| `user` | `/api/auth/**`, `/api/users/**` | 인증, 회원가입, 프로필 관리 |
| `quiz` | `/api/quiz/**` | 문제 은행 관리 |
| `lesson` | `/api/lesson/**` | 교안 뷰어 |

## global 책임 요약

| 패키지 | 주요 역할 |
|--------|-----------|
| `jwt` | 토큰 생성·검증·파싱, 요청 필터링 |
| `oauth2` | 카카오 소셜 로그인 처리 |
| `security` | FilterChain 구성, RBAC 적용 |
| `exception` | 공통 예외 처리 |
| `common` | 공통 응답 규격 |

---

## 설계 원칙

- **Role 정의** (`enums/Role.java`)는 `domain/user/`에 위치 — 비즈니스 데이터
- **Role 강제** (`SecurityConfig.java`)는 `global/security/`에 위치 — 횡단 관심사
- `quiz/`, `lesson/` 도메인은 인증·권한 로직에 의존하지 않음
- DTO는 `request/` / `response/`로 분리
- Service는 의존성 그래프 기준으로 분리
  - `UserAuthService` — 로그인, 토큰 재발급, 로그아웃
  - `UserSignupService` — 회원가입
  - `UserService` — 프로필 조회, 수정, 탈퇴
- Controller는 URL 경로 기준으로 분리
  - `AuthController` — `/api/auth/**`
  - `UserController` — `/api/users/**`
