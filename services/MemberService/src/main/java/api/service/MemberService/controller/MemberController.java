package api.service.MemberService.controller;

import api.service.MemberService.dto.member.MemberDto;
import api.service.MemberService.dto.member.MemberRequestDto;
import api.service.MemberService.dto.member.MemberResponseDto;
import api.service.MemberService.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 정보를 조회하는 메서드이다.
     * 클라이언트로부터 전달받은 이메일을 통해 회원 정보를 조회한다.
     *
     * @param email 조회할 회원의 이메일
     * @return 회원 정보 조회 결과를 담은 응답 DTO와 HTTP 200 상태 코드 반환
     */
    @GetMapping("/findMember")
    public ResponseEntity<MemberResponseDto> findMember(@RequestParam String email) {
        MemberDto memberInfo = memberService.findMember(email);
        return new ResponseEntity<>(MemberResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("success find member info")
                .memberInfo(memberInfo).build(),
                HttpStatus.OK);
    }

    /**
     * 회원 정보를 수정하는 메서드이다.
     * 클라이언트로부터 전달받은 회원 정보를 수정한다.
     *
     * @param memberRequestDto 수정할 회원 정보를 담은 DTO
     * @return 회원 정보 수정 결과를 담은 응답 DTO와 HTTP 200 상태 코드 반환
     */
    @PutMapping("/updateMember")
    public ResponseEntity<MemberResponseDto> updateMember(@RequestBody MemberRequestDto memberRequestDto) {
        memberService.updateMember(memberRequestDto);
        return new ResponseEntity<>(MemberResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("success update member info").build(),
                HttpStatus.OK);
    }

    /**
     * 비밀번호를 변경하는 메서드이다.
     * 클라이언트로부터 전달받은 이메일과 비밀번호를 통해 비밀번호를 변경한다.
     *
     * @param email 비밀번호를 변경할 회원의 이메일
     * @param password 변경할 비밀번호
     * @return 비밀번호 변경 결과를 담은 응답 DTO와 HTTP 200 상태 코드 반환
     */
    @PutMapping("/changePassword")
    public ResponseEntity<MemberResponseDto> changePassword(@RequestParam String email, @RequestParam String password) {
        memberService.changePassword(email, password);
        return new ResponseEntity<>(MemberResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("success update password").build(),
                HttpStatus.OK);
    }

    /**
     * 회원 정보를 삭제하는 메서드이다.
     * 클라이언트로부터 전달받은 이메일을 통해 회원 정보를 삭제한다.
     *
     * @param email 삭제할 회원의 이메일
     * @return 회원 정보 삭제 결과를 담은 응답 DTO와 HTTP 200 상태 코드 반환
     */
    @DeleteMapping("/deleteMember")
    public ResponseEntity<MemberResponseDto> deleteMember(@RequestParam String email) {
        memberService.deleteMember(email);
        return new ResponseEntity<>(MemberResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("success delete member").build(),
                HttpStatus.OK);
    }

    /**
     * IllegalArgumentException 예외 처리 핸들러이다.
     * 회원 정보 조회, 수정, 삭제, 비밀번호 변경 과정에서 발생하는 예외를 처리한다.
     *
     * @param e IllegalArgumentException 예외 객체
     * @return 에러 메시지를 담은 응답 DTO와 HTTP 400 상태 코드 반환
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MemberResponseDto> handleMemberNotFounded(IllegalArgumentException e) {
        return new ResponseEntity<>(MemberResponseDto.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }
}
