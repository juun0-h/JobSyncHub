package api.service.MemberService.service;

import api.service.MemberService.dto.member.MemberDto;
import api.service.MemberService.dto.member.MemberRequestDto;
import api.service.MemberService.dto.tag.TagDto;
import api.service.MemberService.entity.Member;
import api.service.MemberService.entity.Tag;
import api.service.MemberService.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 정보를 DB에서 조회하여 반환하는 서비스 메서드이다.
     *
     * @param email 조회할 회원의 이메일
     * @return 조회된 회원 정보를 담은 DTO
     * @throws IllegalArgumentException 회원 정보가 존재하지 않을 경우 예외 발생
     */
    public MemberDto findMember(String email) {
        return memberRepository.findByEmail(email).map(member -> {
            return MemberDto.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .created(member.getCreated())
                    .tags(member.getTags().stream().map(tag -> TagDto.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .build()).toList())
                    .build();

        }).orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    /**
     * 회원 정보를 수정하는 서비스 메서드이다.
     *
     * @param memberRequestDto 수정할 회원 정보를 담은 DTO
     * @throws IllegalArgumentException 회원 정보가 존재하지 않을 경우 예외 발생
     */
    public void updateMember(MemberRequestDto memberRequestDto) {
        Member member = memberRepository.findByEmail(memberRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        member.setEmail(memberRequestDto.getEmail());
        member.setName(memberRequestDto.getName());
        member.setUpdated(Timestamp.valueOf(LocalDateTime.now()));
        member.setSubscribed(memberRequestDto.getSubscribed());

        List<Tag> tags = member.getTags();
        tags.clear();

        memberRequestDto.getTags().forEach(tagDto -> {
            Tag tag = Tag.builder()
                    .id(tagDto.getId())
                    .name(tagDto.getName())
                    .build();
            tags.add(tag);
        });
        member.setTags(tags);

        memberRepository.save(member);
    }

    /**
     * 비밀번호를 변경하는 서비스 메서드이다.
     *
     * @param email 비밀번호를 변경할 회원의 이메일
     * @param password 변경할 비밀번호
     * @throws IllegalArgumentException 회원 정보가 존재하지 않을 경우 예외 발생
     */
    public void changePassword(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("user not found"));
        member.setPassword(passwordEncoder.encode(password));

        memberRepository.save(member);
    }

    /**
     * 회원 정보를 삭제하는 서비스 메서드이다.
     *
     * @param email 삭제할 회원의 이메일
     * @throws IllegalArgumentException 회원 정보가 존재하지 않을 경우 예외 발생
     */
    public void deleteMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        memberRepository.delete(member);
    }
}
