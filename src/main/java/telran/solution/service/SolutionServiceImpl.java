package telran.solution.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telran.solution.dao.SolutionRepository;
import telran.solution.dto.CreateEditSolutionDto;
import telran.solution.dto.SolutionDto;
import telran.solution.dto.exceptions.SolutionNotFoundException;
import telran.solution.kafka.KafkaConsumer;
import telran.solution.kafka.KafkaProducer;
import telran.solution.kafka.kafkaDataDto.problemDataDto.ProblemServiceDataDto;
import telran.solution.kafka.kafkaDataDto.solutionDataDto.SolutionMethodName;
import telran.solution.kafka.kafkaDataDto.solutionDataDto.SolutionServiceDataDto;
import telran.solution.kafka.profileDataDto.ProfileDataDto;
import telran.solution.model.Solution;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionServiceImpl implements SolutionService {
    final SolutionRepository solutionRepository;
    final KafkaConsumer kafkaConsumer;
    final KafkaProducer kafkaProducer;
    final ModelMapper modelMapper;

    @Override
    @Transactional
    public SolutionDto addSolution(String problemId, CreateEditSolutionDto details) {
        Solution solution = modelMapper.map(details, Solution.class);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        solution.setAuthor(profile.getUserName());
        solution.setAuthorId(profile.getEmail());
        solution.setProblemId(problem.getProblemId());
        solutionRepository.save(solution);
        transferData(profile, problem, solution, SolutionMethodName.ADD_SOLUTION);
        return modelMapper.map(solution, SolutionDto.class);
    }


    @Override
    @Transactional
    public SolutionDto editSolution(String problemId, String solutionId, CreateEditSolutionDto details) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(SolutionNotFoundException::new);
        solution.setDetails(details.getDetails());
        solutionRepository.save(solution);
        return modelMapper.map(solution, SolutionDto.class);
    }


    @Override
    @Transactional
    public boolean addLike(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        Double profileRating = profile.getRating();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        boolean result = solution.getReactions().setLike(profile.getEmail(), profileRating);
        if (solution.checkSolutionResult(problem.getProblemRating())){
            System.out.println("SOLUTION CHECKED AND PROBLEM CLOSED");
            // transferData(profile, problem, solution, SolutionMethodName.ADD_DISLIKE); CHECKED
        }
        solutionRepository.save(solution);
        transferData(profile, problem, solution, SolutionMethodName.ADD_LIKE);
        return result;
    }

    @Override
    @Transactional
    public boolean addDisLike(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        Double profileRating = profile.getRating();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        boolean result = solution.getReactions().setDislike(profile.getEmail(), profileRating);
        if (solution.checkSolutionResult(problem.getProblemRating())){
            System.out.println("SOLUTION CHECKED AND PROBLEM CLOSED");
           // transferData(profile, problem, solution, SolutionMethodName.ADD_DISLIKE); CHECKED
        }
        solutionRepository.save(solution);
        transferData(profile, problem, solution, SolutionMethodName.ADD_DISLIKE);
        return result;
    }

    @Override
    @Transactional
    public SolutionDto deleteSolution(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        transferData(profile, problem, solution, SolutionMethodName.DELETE_SOLUTION);
        solutionRepository.delete(solution);
        return modelMapper.map(solution, SolutionDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public SolutionDto getSolution(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        return modelMapper.map(solution, SolutionDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<SolutionDto> getSolutions(String problemId) {
        return solutionRepository.findAllByProblemId(problemId).map(e -> modelMapper.map(e, SolutionDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<SolutionDto> getSolutionsByProfileId(String profileId) {
        return solutionRepository.findAllByAuthorId(profileId).map(e -> modelMapper.map(e, SolutionDto.class)).collect(Collectors.toSet());
    }

    private void transferData(ProfileDataDto profile, ProblemServiceDataDto problem, Solution solution, SolutionMethodName methodName) {
        SolutionServiceDataDto solutionData = new SolutionServiceDataDto(profile.getEmail(), problem.getProblemId(), problem.getProblemRating(), solution.getId(), methodName);
        kafkaProducer.setSolutionData(solutionData);
    }
}
