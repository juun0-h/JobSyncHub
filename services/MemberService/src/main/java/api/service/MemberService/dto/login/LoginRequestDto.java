package api.service.MemberService.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequestDto {

    @NotBlank
    private final String email;
    @NotBlank
    private final String password;
}
