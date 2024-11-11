package api.service.EmailService.dto.verify;

import lombok.Getter;

@Getter
public class VerifyRequestDto {

    private String email;
    private String code;
}
