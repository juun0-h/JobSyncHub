package api.service.MemberService.controller;

import api.service.MemberService.dto.login.LoginRequestDto;
import api.service.MemberService.dto.login.LoginResponseDto;
import api.service.MemberService.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 로그인 요청을 처리하는 컨트롤러이다.
 *
 * @author jinhyeok
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * 로그인 요청을 처리하는 메서드이다.
     * 클라이언트로부터 전달받은 로그인 요청 데이터를 검증하고, 유효한 경우 로그인 처리를 수행한다.
     * 요청 필드가 잘못된 경우 400 (Bad Request) 응답을 반환하고, 로그인에 실패하면 401 (Unauthorized) 응답을 반환한다.
     *
     * @param loginRequestDto 로그인 요청 정보를 담은 DTO
     * @param bindingResult 요청 데이터의 검증 결과를 담은 객체
     * @return 로그인 처리 결과를 담은 응답 DTO와 HTTP 상태 코드
     *
     * @throws Exception 로그인 과정에서 발생하는 예외 처리 401 (Unauthorized) 응답 반환
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginMember(@Validated @RequestBody LoginRequestDto loginRequestDto,
                                                           BindingResult bindingResult) {
        log.info("login {} {}", loginRequestDto.getEmail(), loginRequestDto.getPassword());
        LoginResponseDto loginResponseDto;

        if(bindingResult.hasErrors()){
            // 400 에러 반환
            return new ResponseEntity<>(LoginResponseDto.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("field error request").build(),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            loginResponseDto = loginService.login(loginRequestDto);
        } catch (Exception e) {
            return new ResponseEntity<>(LoginResponseDto.builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("member does not exist").build(),
                    HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        log.info("test");

        return new ResponseEntity<>(Map.of("message", "success test"), HttpStatus.OK);
    }
}
