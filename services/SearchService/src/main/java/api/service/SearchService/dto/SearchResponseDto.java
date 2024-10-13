package api.service.SearchService.dto;

import api.service.SearchService.document.JobPosting;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto {

    private int statusCode;
    private String message;
    private List<JobPosting> jobPostings;
    private Object[] searchAfter;
}
