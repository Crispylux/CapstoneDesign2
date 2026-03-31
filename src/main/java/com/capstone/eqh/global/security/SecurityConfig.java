/**
 * @EnableMethodSecurity: UserController의 @PreAuthorize("hasRole('ADMIN')") 동작을 위해 필수
 * PasswordEncoder Bean을 여기서 정의 → 모든 서비스 레이어에서 주입 가능, 순환 참조 방지
 * Phase 2 작업(JwtAuthenticationFilter, OAuth2 설정)을 TODO 주석으로 명시해 다음 단계 진입점 확보
 * allowedOriginPatterns("*") + allowCredentials(true) 조합 사용 (allowedOrigins("*")는 credentials와 함께 사용 불가)
 */

// global/security/SecurityConfig.java
package com.capstone.eqh.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // @PreAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT Stateless 구조에서 불필요)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 엔드포인트 접근 제어
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/login/oauth2/**").permitAll()
                        // TODO: Phase 2에서 JwtAuthenticationFilter 추가 후 아래 완성
                        .anyRequest().authenticated()
                );

        // TODO: Phase 2 - OAuth2 로그인 설정 추가
        // .oauth2Login(oauth2 -> oauth2
        //     .userInfoEndpoint(userInfo -> userInfo
        //         .userService(customOAuth2UserService))
        //     .successHandler(oAuth2SuccessHandler))

        // TODO: Phase 2 - JwtAuthenticationFilter 추가
        // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));  // 개발 단계 전체 허용, 운영 시 프론트엔드 도메인으로 교체
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}