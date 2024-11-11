package api.service.SearchService.controller;

import api.service.SearchService.dto.SearchResponseDto;
import api.service.SearchService.dto.SearchResult;
import api.service.SearchService.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 채용 공고를 검색하는 API이다.
     *
     * @param title 채용공고 제목
     * @param skills 채용공고에서 요구하는 기술 목록
     * @param experienceTypes 채용공고에서 요구하는 경력 유형 목록(신입, 경력, 무관)
     * @param sortByDeadline 마감일 순으로 정렬하는 방법 (-1: 내림차순, 0: 상시채용, 1: 오름차순)
     * @param searchAfter 검색 이후의 결과를 가져오기 위한 값
     * @return ResponseEntity<SearchResponseDto> 검색 결과
     */
    @GetMapping("/job/search")
    public ResponseEntity<SearchResponseDto> searchRecruitment(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "skills", required = false) List<String> skills,
            @RequestParam(value = "experienceTypes", required = false) List<String> experienceTypes,
            @RequestParam(value = "sortByDeadline", required = false) Integer sortByDeadline,
            @RequestParam(value = "searchAfter", required = false) Object[] searchAfter)
    {
        log.info("Search recruitment");

        try {
            SearchResult searchResult = searchService.searchJob(title, skills, experienceTypes, sortByDeadline, searchAfter);
            log.info("Search job postings: {}", searchResult);

            return new ResponseEntity<>(SearchResponseDto.builder()
                    .statusCode(200)
                    .message("success to search job postings")
                    .jobPostings(searchResult.getJobPostings())
                    .searchAfter(searchResult.getSearchAfter())
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to search job postings", e);
            return new ResponseEntity<>(SearchResponseDto.builder()
                    .statusCode(400)
                    .message("failed to search job postings")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }
}
