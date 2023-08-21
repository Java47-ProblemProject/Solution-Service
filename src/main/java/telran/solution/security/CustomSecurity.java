package telran.solution.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.solution.configuration.KafkaConsumer;
import telran.solution.dao.SolutionRepository;
import telran.solution.dto.accounting.ProfileDto;
import telran.solution.dto.problems.ProblemDto;
import telran.solution.model.Solution;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomSecurity {
    final KafkaConsumer kafkaConsumer;
    final SolutionRepository solutionRepository;

    public boolean checkSolutionAuthorAndProblemId(String problemId, String solutionId, String authorId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemDto problem = kafkaConsumer.getProblem();
        return authorId.equals(profile.getEmail()) && authorId.equals(solution.getAuthorId()) && problemId.equals(problem.getId());
    }

    public boolean checkProblemId(String problemId){
        ProblemDto problem = kafkaConsumer.getProblem();
        return problemId.equals(problem.getId());
    }
}
