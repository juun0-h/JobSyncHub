package api.service.EmailService.controller;

import api.service.EmailService.dto.verify.VerifyRequestDto;
import api.service.EmailService.dto.verify.VerifyResponseDto;
import api.service.EmailService.service.EmailAuthService;
import api.service.EmailService.service.SignUpTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailService;
    private final SignUpTokenService signUpTokenService;


    /**
     * 이메일 인증 요청
     *
     * @param email 이메일 주소
     * @return String 인증 이메일 전송 결과
     */
    @GetMapping("/auth")
    public String authenticateEmail(@RequestParam String email) {
        log.info("이메일 인증 요청: {}", email);

        CompletableFuture<String> sendEmail = emailService.sendAuthEmail(email, "이메일 인증");
        return sendEmail.getNow("인증 이메일 전송");
    }

    /**
     * 이메일 인증 확인
     *
     * @param dto 이메일 주소와 코드
     * @return ResponseEntity<VerifyResponseDto> 인증 결과 반환
     */
    @PostMapping("/verify")
    public ResponseEntity<VerifyResponseDto> verifyEmail(@RequestBody VerifyRequestDto dto) {
        log.info("이메일 인증 확인: {}", dto.getEmail());

        try {
            // 코드 검증
            if (dto.getCode() != null && emailService.verifyCode(dto.getCode(), dto.getEmail())) {
                String token = signUpTokenService.createToken(dto.getEmail());
                return new ResponseEntity<>(VerifyResponseDto.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("success verify email")
                        .token(token)
                        .build(), HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("이메일 인증 실패: {}", e.getMessage());
        }
        return new ResponseEntity<>(VerifyResponseDto.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("fail verify email")
                .build(), HttpStatus.UNAUTHORIZED);
    }
}
