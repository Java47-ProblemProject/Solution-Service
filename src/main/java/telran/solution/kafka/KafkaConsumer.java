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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
@Configuration
@RequiredArgsConstructor
public class KafkaConsumer {
    private final SolutionCustomRepository solutionCustomRepository;
    private final SolutionRepository solutionRepository;
    private final KafkaProducer kafkaProducer;
    private final JwtTokenService jwtTokenService;
    private final Map<String, ProfileDataDto> profiles = new ConcurrentHashMap<>();
    private ProblemServiceDataDto problemData;

    @Bean
    @Transactional
    protected Consumer<ProfileDataDto> receiveProfile() {
        return data -> {
            String email = data.getEmail();
            String userName = data.getUserName();
            ProfileMethodName methodName = data.getMethodName();
            ProfileDataDto profile = this.profiles.get(email);
            if (!profiles.containsKey(email)) {
                this.profiles.put(email, data);
                profile = data;
            }
            if (methodName.equals(ProfileMethodName.SET_PROFILE)) {
                jwtTokenService.setCurrentProfileToken(email, data.getToken());
                this.profiles.get(email).setToken("");
            } else if (methodName.equals(ProfileMethodName.UNSET_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                this.profiles.remove(email);
            } else if (methodName.equals(ProfileMethodName.UPDATED_PROFILE)) {
                this.profiles.put(email, profile);
            } else if (methodName.equals(ProfileMethodName.EDIT_PROFILE_NAME)) {
                solutionCustomRepository.changeAuthorName(email, userName);
                this.profiles.get(email).setUserName(profile.getUserName());
            } else if (methodName.equals(ProfileMethodName.DELETE_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                solutionCustomRepository.deleteCommentsByAuthorId(email);
                this.profiles.remove(email);
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
