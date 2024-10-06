package api.service.MemberService.dto.member;

import lombok.Getter;

@Getter
public class ChangePasswordDto {

    private String email;
    private String password;
}
