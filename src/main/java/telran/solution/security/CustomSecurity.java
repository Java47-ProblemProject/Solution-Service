package telran.solution.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import telran.solution.dao.SolutionRepository;
import telran.solution.kafka.KafkaConsumer;
import telran.solution.kafka.kafkaDataDto.problemDataDto.ProblemServiceDataDto;
import telran.solution.kafka.profileDataDto.ProfileDataDto;
import telran.solution.model.Solution;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomSecurity {
    final KafkaConsumer kafkaConsumer;
    final SolutionRepository solutionRepository;

    public boolean checkSolutionAuthorAndProblemId(String problemId, String solutionId, String authorId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDataDto profile = kafkaConsumer.getProfiles().get(SecurityContextHolder.getContext().getAuthentication().getName());
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        return authorId.equals(profile.getEmail()) && authorId.equals(solution.getAuthorId()) && problemId.equals(problem.getProblemId());
    }

    public boolean checkProblemId(String problemId){
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        return problemId.equals(problem.getProblemId());
    }
}
