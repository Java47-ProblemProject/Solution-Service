package telran.solution.kafka.kafkaDataDto.solutionDataDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SolutionServiceDataDto {
    private String profileId;
    private String problemId;
    private Double problemRating;
    private String solutionId;
    private SolutionMethodName methodName;
}
