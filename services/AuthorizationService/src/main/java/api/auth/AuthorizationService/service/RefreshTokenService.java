package api.auth.AuthorizationService.service;

import api.auth.AuthorizationService.entity.RefreshToken;
import api.auth.AuthorizationService.repository.RefreshTokenRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;

/**
 * RefreshToken을 저장하고 조회하는 서비스 클래스이다.
 *
 * @author jinhyeok
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 갱신 토큰 조회
    public RefreshToken findRefreshToken(String email) {
        return refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("not found refresh token"));
    }

    // 갱신 토큰 저장
    public void saveRefreshToken(RefreshToken token){
        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(token.getRefreshToken())
                .email(token.getEmail()).build());
    }

    // 갱신 토큰 삭제
    public void removeRefreshToken(String email){
        refreshTokenRepository.findByEmail(email)
                .ifPresent(refreshTokenRepository::delete);
    }
}
