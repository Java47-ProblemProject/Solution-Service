package telran.solution.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import telran.solution.dao.SolutionCustomRepository;
import telran.solution.dao.SolutionRepository;
import telran.solution.kafka.kafkaDataDto.problemDataDto.ProblemMethodName;
import telran.solution.kafka.kafkaDataDto.problemDataDto.ProblemServiceDataDto;
import telran.solution.kafka.profileDataDto.ProfileDataDto;
import telran.solution.kafka.profileDataDto.ProfileMethodName;
import telran.solution.security.JwtTokenService;

import java.util.function.Consumer;

@Getter
@Configuration
@RequiredArgsConstructor
public class KafkaConsumer {
    private final SolutionCustomRepository solutionCustomRepository;
    private final SolutionRepository solutionRepository;
    private final KafkaProducer kafkaProducer;
    private final JwtTokenService jwtTokenService;
    private ProfileDataDto profile;
    private ProblemServiceDataDto problemData;

    @Bean
    @Transactional
    protected Consumer<ProfileDataDto> receiveProfile() {
        return data -> {
            ProfileMethodName methodName = data.getMethodName();
            String userName = data.getUserName();
            String email = data.getEmail();
            if (methodName.equals(ProfileMethodName.SET_PROFILE)) {
                jwtTokenService.setCurrentProfileToken(data.getEmail(), data.getToken());
                this.profile = data;
                this.profile.setToken("");
            } else if (methodName.equals(ProfileMethodName.UNSET_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                this.profile = null;
            } else if (methodName.equals(ProfileMethodName.UPDATED_PROFILE)) {
                this.profile = data;

            } else if (methodName.equals(ProfileMethodName.EDIT_PROFILE_NAME)) {
                solutionCustomRepository.changeAuthorName(email, userName);
                this.profile.setUserName(data.getUserName());
            } else if (methodName.equals(ProfileMethodName.DELETE_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                solutionCustomRepository.deleteCommentsByAuthorId(email);
                this.profile = null;
            }
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
