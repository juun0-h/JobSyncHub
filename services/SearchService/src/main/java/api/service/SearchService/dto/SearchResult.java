package api.service.SearchService.dto;

import api.service.SearchService.document.JobPosting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    private List<JobPosting> jobPostings;
    private Object[] searchAfter;
}
