package com.capstone.eqh.domain.user.service;

import com.capstone.eqh.domain.user.dto.request.LoginRequest;
import com.capstone.eqh.domain.user.dto.request.LogoutRequest;
import com.capstone.eqh.domain.user.dto.request.ReissueRequest;
import com.capstone.eqh.domain.user.dto.response.AuthResponse;
import com.capstone.eqh.domain.user.entity.RefreshToken;
import com.capstone.eqh.domain.user.entity.User;
import com.capstone.eqh.domain.user.enums.AuthProvider;
import com.capstone.eqh.domain.user.repository.RefreshTokenRepository;
import com.capstone.eqh.domain.user.repository.UserRepository;
import com.capstone.eqh.global.exception.CustomException;
import com.capstone.eqh.global.exception.ErrorCode;
import com.capstone.eqh.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new CustomException(ErrorCode.SOCIAL_ACCOUNT_CONFLICT);
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        saveOrUpdateRefreshToken(user.getId(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse reissue(ReissueRequest request) {
        String oldToken = request.refreshToken();

        Long userId = jwtProvider.getUserIdIgnoringExpiry(oldToken);

        RefreshToken stored = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(stored);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId());

        stored.updateToken(newRefreshToken, toLocalDateTime(jwtProvider.getExpiration(newRefreshToken)));

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenRepository.findByToken(request.refreshToken())
                .ifPresent(refreshTokenRepository::delete);
    }

    private void saveOrUpdateRefreshToken(Long userId, String token) {
        LocalDateTime expiryDate = toLocalDateTime(jwtProvider.getExpiration(token));

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        existing -> existing.updateToken(token, expiryDate),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .token(token)
                                        .userId(userId)
                                        .expiryDate(expiryDate)
                                        .build()
                        )
                );
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
