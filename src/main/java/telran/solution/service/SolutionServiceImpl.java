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
import telran.solution.dto.accounting.ActivityDto;
import telran.solution.dto.accounting.ProfileDto;
import telran.solution.dto.exceptions.SolutionNotFoundException;
import telran.solution.dto.problems.ProblemDto;
import telran.solution.dto.solutions.CreateEditSolutionDto;
import telran.solution.dto.solutions.SolutionDto;
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
        ProblemDto problem = kafkaConsumer.getProblem();
        if (problem.getId().equals(problemId)) {
            solution.setAuthor(profile.getUsername());
            solution.setAuthorId(profile.getEmail());
            solutionRepository.save(solution);
            profile.addActivity(solution.getId(), new ActivityDto(solution.getType(), false, false));
            editProfile(profile);
            kafkaProducer.setSolutionIdToProblem(problemId + "," + solution.getId());
            kafkaProducer.sendUpdatedProfile();
            return modelMapper.map(solution, SolutionDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Wrong problem in address");
    }


    @Override
    public SolutionDto editSolution(String problemId, String solutionId, CreateEditSolutionDto details) {
        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(SolutionNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (solution.getAuthorId().equals(profile.getEmail())) {
            solution.setDetails(details.getDetails());
            solutionRepository.save(solution);
            return modelMapper.map(solution, SolutionDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not the author of this solution");
    }


    @Override
    public boolean addLike(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ActivityDto activity = profile.getActivities().computeIfAbsent(solutionId, a -> new ActivityDto(solution.getType(), false, false));
        if (!activity.getLiked()) {
            activity.setLiked(true);
            if (activity.getDisliked()) {
                activity.setDisliked(false);
                solution.getReactions().removeDislike();
            }
            solution.getReactions().addLike();
            solutionRepository.save(solution);
            profile.addActivity(solutionId, activity);
            editProfile(profile);
            return true;
        }
        return false;

    }

    @Override
    public boolean addDisLike(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ActivityDto activity = profile.getActivities().computeIfAbsent(solutionId, a -> new ActivityDto(solution.getType(), false, false));
        if (!activity.getDisliked()) {
            activity.setDisliked(true);
            if (activity.getLiked()) {
                activity.setLiked(false);
                solution.getReactions().removeLike();
            }
            solution.getReactions().addDislike();
            solutionRepository.save(solution);
            profile.addActivity(solutionId, activity);
            editProfile(profile);
            return true;
        }
        return false;
    }

    @Override
    public SolutionDto deleteSolution(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (solution.getAuthorId().equals(profile.getEmail())) {
            profile.removeActivity(solutionId);
            //kafkaProducer.setSolutionIdToDelete(solutionId);
            editProfile(profile);
            solutionRepository.delete(solution);
            return modelMapper.map(solution, SolutionDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not the author of this solution");
    }

    @Override
    public SolutionDto getSolution(String problemId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NoSuchElementException::new);
        return modelMapper.map(solution, SolutionDto.class);
    }

    @Override
    public Set<SolutionDto> getSolutions(String problemId) {
        return solutionRepository.findAll().stream().map(e -> modelMapper.map(e, SolutionDto.class))
                .collect(Collectors.toSet());
    }

    private void editProfile(ProfileDto profile) {
        kafkaConsumer.setProfile(profile);
        kafkaProducer.setProfile(profile);
    }
}
