package api.service.EmailService.dto;

import lombok.Getter;

@Getter
public class VerifyRequestDto {
    private String email;
    private String code;
}
