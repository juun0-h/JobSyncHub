package api.service.MemberService.service;

import api.service.MemberService.dto.member.MemberDto;
import api.service.MemberService.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 로그인한 회원의 상세 정보를 담고 있는 클래스이다.
 * Spring Security의 {@link User} 클래스를 상속받아, 추가적으로 {@link MemberDto} 객체를 통해 회원의 상세 정보를 포함한다.
 *
 * @author jinhyeok
 */
@Getter
public class LoginMemberDetail extends User {

    private final MemberDto memberDto;

    /**
     * 주어진 {@link Member} 엔티티와 권한 정보로 {@link LoginMemberDetail} 객체를 생성한다.
     *
     * @param member 로그인한 회원의 정보를 담은 엔티티 객체
     * @param authorities 회원의 권한 목록
     */
    public LoginMemberDetail(Member member, Collection<? extends GrantedAuthority> authorities) {
        super(member.getEmail(), member.getPassword(), authorities);
        this.memberDto = MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .created(member.getCreated())
                .build();
    }
}
