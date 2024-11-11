package api.service.EmailService.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpTokenService {

    @Value("${jwt.expire-time}")
    private long expireTime;
    @Value("${jwt.signup.secret-key}")
    private String signupSecretKey;
    private Key signature;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    /**
     * 회원가입 요청 검증을 위한 jwt 토큰 생성
     * 해당 토큰 검증은 MemberService 에서 수행
     *
     * @param email 토큰에 포함될 사용자 이메일
     * @return 생성된 JWT 토큰 문자열 반환
     */
    public String createToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .claim("use", "SignUp")
                .signWith(signature, signatureAlgorithm)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .compact();
    }

    @PostConstruct
    private void initSignature() throws Exception {
        // Base64로 인코딩된 비밀 키를 디코딩하여 바이트 배열로 저장
        byte[] keyByte = Decoders.BASE64.decode(signupSecretKey);

        //디코딩된 비밀키와 서명 알고리즘을 넣어 Key(=서명) 생성
        //SecretKeySpec : Jwt를 서명하거나 서명을 검증하는 데 사용
        signature = new SecretKeySpec(keyByte, SignatureAlgorithm.HS256.getJcaName());
    }
}
