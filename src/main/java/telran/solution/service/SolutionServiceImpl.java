package telran.solution.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import telran.solution.kafka.KafkaConsumer;
import telran.solution.kafka.KafkaProducer;
import telran.solution.dao.SolutionRepository;
import telran.solution.kafka.accounting.ProfileDto;
import telran.solution.dto.exceptions.SolutionNotFoundException;
import telran.solution.kafka.kafkaDataDto.problemDataDto.ProblemServiceDataDto;
import telran.solution.kafka.kafkaDataDto.solutionDataDto.SolutionMethodName;
import telran.solution.kafka.kafkaDataDto.solutionDataDto.SolutionServiceDataDto;
import telran.solution.dto.CreateEditSolutionDto;
import telran.solution.dto.SolutionDto;
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
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        if (problem.getProblemId().equals(problemId)) {
            solution.setAuthor(profile.getUsername());
            solution.setAuthorId(profile.getEmail());
            solution.setProblemId(problem.getProblemId());
            solutionRepository.save(solution);
            SolutionServiceDataDto data = addDataToTransfer(profile, problem, solution, SolutionMethodName.ADD_SOLUTION);
            kafkaProducer.setSolutionData(data);
            return modelMapper.map(solution, SolutionDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Wrong problem in address");
    }


    @Override
    @Transactional
    public SolutionDto editSolution(String problemId, String solutionId, CreateEditSolutionDto details) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(SolutionNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (solution.getAuthorId().equals(profile.getEmail())) {
            solution.setDetails(details.getDetails());
            solutionRepository.save(solution);
            return modelMapper.map(solution, SolutionDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not the author of this solution");
    }


    @Override
    @Transactional
    public boolean addLike(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        Double profileRating = profile.getStats().getRating();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        boolean result = solution.getReactions().setLike(profile.getEmail(), profileRating);
        solutionRepository.save(solution);
        SolutionServiceDataDto data = addDataToTransfer(profile, problem, solution, SolutionMethodName.ADD_LIKE);
        kafkaProducer.setSolutionData(data);
        return result;
    }

    @Override
    @Transactional
    public boolean addDisLike(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        Double profileRating = profile.getStats().getRating();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        boolean result = solution.getReactions().setDislike(profile.getEmail(), profileRating);
        solutionRepository.save(solution);
        SolutionServiceDataDto data = addDataToTransfer(profile, problem, solution, SolutionMethodName.ADD_DISLIKE);
        kafkaProducer.setSolutionData(data);
        return result;
    }

    @Override
    @Transactional
    public SolutionDto deleteSolution(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        if (solution.getAuthorId().equals(profile.getEmail())) {
            SolutionServiceDataDto data = addDataToTransfer(profile, problem, solution, SolutionMethodName.DELETE_SOLUTION);
            if (problem.getSubscribers().contains(profile.getEmail())) {
                data = addDataToTransfer(profile, problem, solution, SolutionMethodName.DELETE_SOLUTION_AND_PROBLEM);
            }
            kafkaProducer.setSolutionData(data);
            solutionRepository.delete(solution);
            return modelMapper.map(solution, SolutionDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not the author of this solution");
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
        return solutionRepository.findAll().stream().map(e -> modelMapper.map(e, SolutionDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<SolutionDto> getSolutionsByProfileId(String profileId) {
        return solutionRepository.findAllByAuthorId(profileId).map(e -> modelMapper.map(e, SolutionDto.class)).collect(Collectors.toSet());
    }

    private SolutionServiceDataDto addDataToTransfer(ProfileDto profile,ProblemServiceDataDto problem, Solution solution, SolutionMethodName methodName) {
        return new SolutionServiceDataDto(profile.getEmail(), problem.getProblemId(), problem.getProblemRating(), solution.getId(), methodName);
    }
}
