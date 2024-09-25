package api.service.MemberService.service;

import api.service.MemberService.dto.signup.SignUpRequestDto;
import api.service.MemberService.entity.Member;
import api.service.MemberService.entity.Role;
import api.service.MemberService.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자의 회원가입 요청을 처리하는 서비스 클래스이다.
 *
 * @author jinhyeok
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SignService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자의 회원가입 요청을 처리하는 메서드이다.
     * 입력된 회원 정보를 저장하고 저장된 회원 정보를 반환한다.
     *
     * @param signUpRequestDto
     * @return Member 새로 회원가입한 회원 정보 반환
     * @throws IllegalArgumentException 이미 가입된 회원인 경우 예외 발생
     */
    public Member signupMember(SignUpRequestDto signUpRequestDto) {

        // 이미 가입된 회원인지 확인
        // 이미 가입된 회원이면 예외 발생 -> controller에서 예외 처리
        memberRepository.findByEmail(signUpRequestDto.getEmail())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("user already exists");
                });

        // 회원 정보 생성
        Member member = Member.builder()
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .name(signUpRequestDto.getName())
                .role(Role.USER)    // 기본 권한은 USER
                .build();
        // 회원 정보 저장 및 반환
        return memberRepository.save(member);
    }

}
