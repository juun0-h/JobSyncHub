package api.service.MemberService.service;

import api.service.MemberService.entity.Member;
import api.service.MemberService.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * 회원의 인증 정보를 로드하는 {@link UserDetailsService}를 구현한 사용자 정의 서비스 클래스이다.
 *
 * @author jinhyeok
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginMemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 주어진 이메일로 회원 정보를 조회하여 {@link LoginMemberDetail} 객체를 반환한다.
     * 회원이 존재하지 않으면 {@link UsernameNotFoundException}을 던진다.
     *
     * @param email 인증 요청한 사용자의 이메일
     * @return {@link UserDetails} 객체로 반환된 회원의 인증 정보
     * @throws UsernameNotFoundException 주어진 이메일에 해당하는 회원이 존재하지 않을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername {}", email);

        // 주어진 이메일로 회원 정보를 조회하여 반환
        // 회원이 존재하지 않으면 UsernameNotFoundException 발생
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> member not founded"));

        return new LoginMemberDetail(member, Collections.singletonList(() -> member.getRole().toString()));
    }
}
