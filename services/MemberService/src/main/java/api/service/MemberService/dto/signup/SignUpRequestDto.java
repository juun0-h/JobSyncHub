package api.service.MemberService.dto.signup;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    private String picture;
    private String role;
}
