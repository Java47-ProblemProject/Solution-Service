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
            String problemId = data.getProblemId();
            ProblemMethodName method = data.getMethodName();
            if (method.equals(ProblemMethodName.DELETE_PROBLEM)) {
                solutionCustomRepository.deleteSolutionsByProblemId(problemId);
                this.problemData = new ProblemServiceDataDto();
            } else {
                this.problemData = data;
            }
        };
    }
}
