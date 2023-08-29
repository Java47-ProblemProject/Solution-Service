package telran.solution.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import telran.solution.configuration.KafkaConsumer;
import telran.solution.configuration.KafkaProducer;
import telran.solution.dao.SolutionRepository;
import telran.solution.dto.accounting.ProfileDto;
import telran.solution.dto.exceptions.SolutionNotFoundException;
import telran.solution.dto.kafkaData.problemDataDto.ProblemServiceDataDto;
import telran.solution.dto.kafkaData.solutionDataDto.SolutionMethodName;
import telran.solution.dto.kafkaData.solutionDataDto.SolutionServiceDataDto;
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
            solutionRepository.save(solution);
            SolutionServiceDataDto data = addDataToTransfer(profile, problem, solution, SolutionMethodName.ADD_SOLUTION);
            System.out.println("SOLUTION DATA SENT");
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
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        SolutionServiceDataDto data;
        boolean hasActivity = profile.getActivities().containsKey(solutionId);
        if (!hasActivity) {
            solution.getReactions().addLike();
            solutionRepository.save(solution);
            data = addDataToTransfer(profile, problem, solution, SolutionMethodName.ADD_LIKE);
            kafkaProducer.setSolutionData(data);
            return true;
        }
        boolean liked = profile.getActivities().get(solutionId).getLiked();
        boolean disliked = profile.getActivities().get(solutionId).getDisliked();
        if (!liked) {
            solution.getReactions().addLike();
            if (disliked) {
                solution.getReactions().removeDislike();
            }
            data = addDataToTransfer(profile, problem, solution, SolutionMethodName.ADD_LIKE);
            kafkaProducer.setSolutionData(data);
            solutionRepository.save(solution);
            return true;
        } else {
            boolean isSubscriber = problem.getSubscribers().contains(profile.getEmail());
            boolean isAuthorProblem = problem.getProblemAuthorId().equals(profile.getEmail());
            boolean isAuthorComment = solution.getAuthorId().equals(profile.getEmail());
            if (isAuthorComment) {
                data = addDataToTransfer(profile, problem, solution, SolutionMethodName.REMOVE_LIKE);
            } else if (isAuthorProblem || isSubscriber) {
                data = addDataToTransfer(profile, problem, solution, SolutionMethodName.REMOVE_LIKE_REMOVE_COMMENT_ACTIVITY);
            } else {
                data = addDataToTransfer(profile, problem, solution, SolutionMethodName.REMOVE_LIKE_REMOVE_ALL_ACTIVITIES);
            }
            kafkaProducer.setSolutionData(data);
            solution.getReactions().removeLike();
            solutionRepository.save(solution);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean addDisLike(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        SolutionServiceDataDto data;
        boolean hasActivity = profile.getActivities().containsKey(solutionId);
        if (!hasActivity) {
            solution.getReactions().addDislike();
            solutionRepository.save(solution);
            data = addDataToTransfer(profile, problem, solution, SolutionMethodName.ADD_DISLIKE);
            kafkaProducer.setSolutionData(data);
            return true;
        }
        boolean liked = profile.getActivities().get(solutionId).getLiked();
        boolean disliked = profile.getActivities().get(solutionId).getDisliked();
        if (!disliked) {
            solution.getReactions().addDislike();
            if (liked) {
                solution.getReactions().removeLike();
            }
            data = addDataToTransfer(profile, problem, solution, SolutionMethodName.ADD_DISLIKE);
            kafkaProducer.setSolutionData(data);
            solutionRepository.save(solution);
            return true;
        } else {
            boolean isSubscriber = problem.getSubscribers().contains(profile.getEmail());
            boolean isAuthorProblem = problem.getProblemAuthorId().equals(profile.getEmail());
            boolean isAuthorComment = solution.getAuthorId().equals(profile.getEmail());
            if (isAuthorComment) {
                data = addDataToTransfer(profile, problem, solution, SolutionMethodName.REMOVE_DISLIKE);
            } else if (isAuthorProblem || isSubscriber) {
                data = addDataToTransfer(profile, problem, solution, SolutionMethodName.REMOVE_DISLIKE_REMOVE_COMMENT_ACTIVITY);
            } else {
                data = addDataToTransfer(profile, problem, solution, SolutionMethodName.REMOVE_DISLIKE_REMOVE_ALL_ACTIVITIES);
            }
            kafkaProducer.setSolutionData(data);
            solution.getReactions().removeLike();
            solutionRepository.save(solution);
            return false;
        }
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
