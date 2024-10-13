package api.service.SearchService.document;

import lombok.Data;

import java.util.List;

@Data
public class JobPosting {

    private String url;
    private String title;
    private String company;
    private String location;
    private Long deadline_ts;
    private String deadline;
    private String experience;
    private String experienceType;
    private List<String> skills;
}
