package api.service.MemberService.dto.member;

import api.service.MemberService.dto.tag.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

    private String email;
    private String name;
    private List<TagDto> tags;
    private Boolean subscribed;
}
