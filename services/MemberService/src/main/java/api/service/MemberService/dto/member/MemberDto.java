package api.service.MemberService.dto.member;

import api.service.MemberService.dto.tag.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private Long id;
    private String email;
    private String name;
    private Timestamp created;
    private List<TagDto> tags;
}
