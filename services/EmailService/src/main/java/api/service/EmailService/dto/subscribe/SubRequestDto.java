package api.service.EmailService.dto.subscribe;

import lombok.Getter;

import java.util.List;

@Getter
public class SubRequestDto {

    private String email;
    private List<String> tags;
    private Boolean subscribed;
}
