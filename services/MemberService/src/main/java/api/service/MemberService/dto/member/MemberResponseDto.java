package api.service.MemberService.dto.member;

import api.service.MemberService.dto.member.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {

    private int statusCode;
    private String message;
    private MemberDto memberInfo;
}
