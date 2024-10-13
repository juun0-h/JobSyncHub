package api.service.SearchService.service;

import api.service.SearchService.document.JobPosting;
import api.service.SearchService.dto.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    @Value("${opensearch.index}")
    private String index;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    /**
     * 채용 공고를 검색하는 메서드이다.
     * OpenSearch를 사용하여 채용 공고를 검색한다.
     *
     * @param title 채용공고 제목
     * @param skills 채용공고에서 요구하는 기술 목록
     * @param experienceTypes 채용공고에서 요구하는 경력 유형 목록(신입, 경력, 무관)
     * @param sortByDeadline 마감일 순으로 정렬하는 방법 (-1: 내림차순, 0: 상시채용, 1: 오름차순)
     * @param searchAfter 검색 이후의 결과를 가져오기 위한 값
     * @return SearchResult 검색 결과
     *
     * @throws IOException OpenSearch API 호출 중 예외 발생 시 발생
     */
    public SearchResult searchJob(
            String title,
            List<String> skills,
            List<String> experienceTypes,
            Integer sortByDeadline,  // -1: 내림차순, 0: 상시채용, 1: 오름차순
            Object[] searchAfter) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 제목 필터링
        if (title != null && !title.isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("title", title));
        }

        // 스킬 필터링
        if (skills != null && !skills.isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("skills", skills));
        }

        // 경력 유형 필터링
        if (experienceTypes != null && !experienceTypes.isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("experienceType", experienceTypes));
        }

        // sortByDeadline 값에 따른 처리
        if (sortByDeadline != null) {
            if (sortByDeadline == -1) {
                // 마감일 내림차순 정렬
                sourceBuilder.sort("deadline_ts", SortOrder.DESC);
                boolQuery.filter(QueryBuilders.rangeQuery("deadline_ts").lt(Integer.MAX_VALUE));
            } else if (sortByDeadline == 0) {
                // 상시채용 필터링
                boolQuery.filter(QueryBuilders.rangeQuery("deadline_ts").gt(Integer.MAX_VALUE));
            } else if (sortByDeadline == 1) {
                // 마감일 오름차순 정렬
                sourceBuilder.sort("deadline_ts", SortOrder.ASC);
            }
        } else {
            // sortByDeadline이 null인 경우에도 정렬 필드를 지정
            sourceBuilder.sort("deadline_ts", SortOrder.ASC);
        }

        sourceBuilder.sort("_id", SortOrder.ASC);

        // search_after 설정
        if (searchAfter != null && searchAfter.length > 0) {
            log.info("search after");
            sourceBuilder.searchAfter(searchAfter);
        }
        sourceBuilder.size(10);
        sourceBuilder.query(boolQuery);
        searchRequest.source(sourceBuilder);

        // 검색 요청 후 응답 저장
        SearchResponse searchResponse = restHighLevelClient.search(
                searchRequest,
                RequestOptions.DEFAULT.toBuilder()
                        .addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        .build());

        // 검색 결과 파싱하여 source 추출 후 JobPosting 객체로 변환
        List<JobPosting> jobPostings = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for(SearchHit hit : hits){
            try {
                JobPosting job = objectMapper.readValue(hit.getSourceAsString(), JobPosting.class);
                jobPostings.add(job);
            } catch (IOException e) {
                log.info("Failed to parse search response", e);
            }
        }

        // 검색 결과에서 마지막 문서 sort 필드 추출
        Object[] sortValues = null;
        if(hits.length>0){
            SearchHit lastHit = hits[hits.length-1];
            sortValues = lastHit.getSortValues();
        }

        return SearchResult.builder()
                .jobPostings(jobPostings)
                .searchAfter(sortValues)
                .build();
    }
}
