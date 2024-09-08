package api.service.MemberService.service;

import api.service.MemberService.dto.LoginRequestDto;
import api.service.MemberService.dto.LoginResponseDto;
import api.service.MemberService.entity.RefreshToken;
import api.service.MemberService.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 사용자의 로그인 요청을 처리하는 서비스 클래스이다.
 *
 * @author jinhyeok
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-expire-time}")
    private long accessExpireTime;

    @Value("${jwt.refresh-expire-time}")
    private long refreshExpireTime;

    private Key signature;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final AuthenticationManagerBuilder authManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 사용자의 로그인 요청을 처리하는 메서드이다.
     * 입력된 이메일과 비밀번호로 인증을 수행하고, 성공 시 액세스 토큰과 리프레시 토큰을 생성하여 반환한다.
     * 생성된 리프레시 토큰은 Redis에 저장된다.
     *
     * <p>로그인 절차:</p>
     * <ul>
     *     <li>이메일과 비밀번호를 이용하여 인증 토큰을 생성</li>
     *     <li>인증이 성공하면 SecurityContextHolder에 인증 정보 저장</li>
     *     <li>사용자의 권한 정보를 기반으로 JWT 액세스 토큰 및 리프레시 토큰 생성</li>
     *     <li>리프레시 토큰은 Redis에 저장</li>
     * </ul>
     *
     * @param dto 로그인 요청 정보를 담은 DTO
     * @return 로그인 응답 DTO (상태 코드, 메시지, 액세스 토큰, 사용자 정보 포함)
     * @throws Exception 인증 실패 또는 기타 예외 발생 시 발생
     */
    public LoginResponseDto login(LoginRequestDto dto) throws Exception {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        // CustomUserDetailService 의 loadUser() 실행됨
        Authentication authentication = authManagerBuilder.getObject().authenticate(authenticationToken);

        // 사용자가 로그인한 직후의 인증 상태를 즉시 반영
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 로그인한 사용자의 정보를 가져옴
        // UserDetails 객체를 LoginMemberDetail로 캐스팅
        LoginMemberDetail loginMemberDetail = (LoginMemberDetail) authentication.getPrincipal();

        // 사용자의 권한 정보를 콤마(,)로 구분하여 authorities에 저장
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 액세스 토큰과 리프레시 토큰 생성
        String accessToken = createToken(dto.getEmail(), authorities, accessExpireTime);
        String refreshToken = createToken(dto.getEmail(), authorities, refreshExpireTime);

        // Redis에 refreshToken 저장
        saveRefreshToken(RefreshToken.builder()
                .refreshToken(refreshToken)
                .email(dto.getEmail())
                .build());

        // 로그인 성공 시 응답 DTO 반환
        return LoginResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("success login")
                .accessToken(accessToken)
                .memberInfo(loginMemberDetail.getMemberDto())
                .build();
    }

    // JWT 토큰 생성
    public String createToken(String email, String authorities, long expireTime){
        return Jwts.builder()
                .setSubject(email)
                .claim("auth", authorities)
                .signWith(signature, signatureAlgorithm)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .compact();
    }

    //갱신 토큰 저장
    public void saveRefreshToken(RefreshToken token){
        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(token.getRefreshToken())
                .email(token.getEmail()).build());
    }

    @PostConstruct
    public void initSignature() throws Exception {
        byte[] keyByte = Decoders.BASE64.decode(secretKey);
        signature = new SecretKeySpec(keyByte, SignatureAlgorithm.HS256.getJcaName());
    }
}
