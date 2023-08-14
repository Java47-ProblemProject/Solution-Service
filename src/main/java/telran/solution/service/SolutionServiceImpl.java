package telran.solution.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import telran.solution.dao.SolutionRepository;
import telran.solution.dto.accounting.ProfileDto;
import telran.solution.dto.exceptions.SolutionNotFoundException;
import telran.solution.dto.solutions.CreateSolutionDto;
import telran.solution.dto.solutions.EditSolutionDto;
import telran.solution.dto.solutions.SolutionDto;
import telran.solution.model.Solution;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionServiceImpl implements  SolutionService {
    final SolutionRepository solutionRepository;
    final ModelMapper modelMapper;
//    final KafkaConsumer kafkaConsumer;

    @Override
    public SolutionDto addSolution(CreateSolutionDto solutionDto) {
        Solution solution = modelMapper.map(solutionDto, Solution.class);
        ProfileDto profile = kafkaConsumer.getProfile();
        solution.setAuthor(profile.getUsername());
        solution.setAuthorId(profile.getEmail());
        solution = solutionRepository.save(solution);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return modelMapper.map(solution, SolutionDto.class);
    }

    @Override
    public SolutionDto editSolution(EditSolutionDto editSolutionDto,String userId, String solutionId) {
        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(SolutionNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if(solution.getAuthorId().equals(profile.getEmail())){
            solution.setTitle(editSolutionDto.getTitle());
            solution.setDetails(editSolutionDto.getDetails());
        }
        Solution updatedSolution = solutionRepository.save(solution);
        return modelMapper.map(updatedSolution, SolutionDto.class);
    }

    @Override
    public SolutionDto deleteSolution(String solutionId) {
        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(SolutionNotFoundException::new);
        solutionRepository.delete(solution);
        return modelMapper.map(solution, SolutionDto.class);
    }


    @Override
    public boolean addLike(String solutionId) {
            Solution solution = solutionRepository.findById(solutionId)
                    .orElseThrow(SolutionNotFoundException::new);
            solution.getReactions().addLike();
            solutionRepository.save(solution);
            return true;
    }


    @Override
    public boolean addDisLike(String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(SolutionNotFoundException::new);
        int initialLikes = solution.getReactions().getLikes();
        int initialDislikes = solution.getReactions().getDislikes();
        solution.getReactions().addDislike();
        solutionRepository.save(solution);
        int finalLikes = solution.getReactions().getLikes();
        int finalDislikes = solution.getReactions().getDislikes();

        if (initialLikes == finalLikes && initialDislikes == finalDislikes) {
            return false;
        }

        if (initialLikes > finalLikes) {
            solution.getReactions().subtractLike();
        }

        return true;
    }

    @Override
    public SolutionDto findSolutionById(String solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(SolutionNotFoundException::new);
        return modelMapper.map(solution, SolutionDto.class);
    }

    @Override
    public List<SolutionDto> getSolutions() {
        return solutionRepository.findAll().stream().map(e -> modelMapper.map(e,SolutionDto.class))
                .collect(Collectors.toList());
    }
}
