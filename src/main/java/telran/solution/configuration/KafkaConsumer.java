package telran.solution.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import telran.solution.dao.SolutionCustomRepository;
import telran.solution.dao.SolutionRepository;
import telran.solution.dto.accounting.ProfileDto;
import telran.solution.dto.kafkaData.problemDataDto.ProblemMethodName;
import telran.solution.dto.kafkaData.problemDataDto.ProblemServiceDataDto;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Getter
@Configuration
@RequiredArgsConstructor
public class KafkaConsumer {
    final SolutionCustomRepository solutionCustomRepository;
    final SolutionRepository solutionRepository;
    final KafkaProducer kafkaProducer;
    ProfileDto profile;
    ProblemServiceDataDto problemData;

    @Bean
    @Transactional
    protected Consumer<ProfileDto> receiveProfile() {
        return data -> {
            if (data.getUsername().equals("DELETED_PROFILE")) {
                //profile was deleted ->
                solutionCustomRepository.deleteCommentsByAuthorId(data.getEmail());
                data.getActivities().entrySet().stream()
                        .filter(entry -> "PROBLEM".equals(entry.getValue().getType()) && entry.getValue().getAction().contains("AUTHOR"))
                        .map(Map.Entry::getKey)
                        .forEach(solutionCustomRepository::deleteSolutionsByProblemId);
                this.profile = new ProfileDto();
            } else if (this.profile != null && data.getEmail().equals(profile.getEmail()) && !data.getUsername().equals(profile.getUsername())) {
                solutionCustomRepository.changeAuthorName(data.getEmail(), data.getUsername());
                this.profile = data;
            } else this.profile = data;

        };
    }

    @Bean
    @Transactional
    protected Consumer<ProblemServiceDataDto> receiveDataFromProblem() {
        return data -> {
            String profileId = data.getAuthorizedProfileId();
            String problemId = data.getProblemId();
            ProblemMethodName method = data.getMethodName();
            Set<String> comments = data.getComments();
            Set<String> solutions = data.getSolutions();
            Set<String> subscribers = data.getSubscribers();
            if (method.equals(ProblemMethodName.DELETE_PROBLEM)) {
                solutionCustomRepository.deleteSolutionsByProblemId(problemId);
                this.problemData = new ProblemServiceDataDto();
            } else {
                this.problemData = data;
            }
        };
    }
}
