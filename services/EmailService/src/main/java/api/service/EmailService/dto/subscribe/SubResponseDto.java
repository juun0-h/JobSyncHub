package api.service.EmailService.dto.subscribe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubResponseDto {

    private int statusCode;
    private String message;
    private Boolean subscribed;
}
