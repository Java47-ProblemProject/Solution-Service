package telran.solution.dto.kafkaData.problemDataDto;

import lombok.Getter;

import java.util.Set;

@Getter
public class ProblemServiceDataDto {
    private String authorizedProfileId;
    private String problemId;
    private String problemAuthorId;
    private Double problemRating;
    private ProblemMethodName methodName;
    private Set<String> comments;
    private Set<String> solutions;
    private Set<String> subscribers;
    private Set<String> communities;
}
