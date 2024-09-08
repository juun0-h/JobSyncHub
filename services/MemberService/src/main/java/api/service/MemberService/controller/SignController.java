package api.service.MemberService.controller;

import api.service.MemberService.dto.SignUpRequestDto;
import api.service.MemberService.dto.SignUpResponseDto;
import api.service.MemberService.entity.Member;
import api.service.MemberService.service.SignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원가입 요청을 처리하는 컨트롤러이다.
 *
 * @author jinhyeok
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    /**
     * 회원가입 요청을 처리하는 메서드이다.
     * 클라이언트로부터 전달받은 회원가입 요청 데이터를 검증하고, 유효한 경우 회원가입 처리를 수행한다.
     *
     * @param signUpRequestDto 회원가입 요청 정보를 담은 DTO
     * @param bindingResult 요청 데이터의 검증 결과를 담은 객체
     * @return ResponseEntity<SignUpResponseDto> 회원가입 처리 결과를 담은 응답 DTO와 HTTP 상태 코드 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signUp(@Validated @RequestBody SignUpRequestDto signUpRequestDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            //400 에러 반환
            return new ResponseEntity<>(SignUpResponseDto.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("field error request").build(),
                    HttpStatus.BAD_REQUEST);
        }

        // 회원가입 처리
        Member member = signService.signupMember(signUpRequestDto);

        // 회원가입 성공 응답 반환
        return new ResponseEntity<>(SignUpResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("success signup").build(),
                HttpStatus.OK);
    }

    /**
     * IllegalArgumentException 예외 처리 핸들러이다.
     * IllegalArgumentException 예외가 발생하면 400 (Bad Request) 응답을 반환한다.
     * 동일한 이메일로 가입된 회원이 존재하는 경우 IllegalArgumentException 예외가 발생하는데 이를 처리한다.
     *
     * @param ex
     * @return ResponseEntity<SignUpResponseDto> 400 (Bad Request) 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SignUpResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(SignUpResponseDto.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }
}
