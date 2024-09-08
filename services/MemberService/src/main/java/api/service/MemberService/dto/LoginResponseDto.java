package api.service.MemberService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private int statusCode;
    private String message;
    private String accessToken;
    private MemberDto memberInfo;
}
