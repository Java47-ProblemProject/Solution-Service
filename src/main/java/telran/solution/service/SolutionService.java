package telran.solution.service;

import telran.solution.dto.CreateEditSolutionDto;
import telran.solution.dto.SolutionDto;

import java.util.Set;

public interface SolutionService {
    SolutionDto addSolution(String problemId, CreateEditSolutionDto details);

    boolean addLike(String problemId, String solutionId);

    boolean addDisLike(String problemId, String solutionId);

    SolutionDto editSolution(String problemId, String solutionId, CreateEditSolutionDto details);

    SolutionDto deleteSolution(String problemId, String solutionId);

    SolutionDto getSolution(String problemId, String solutionId);

    Set<SolutionDto> getSolutions(String problemId);

    Set<SolutionDto> getSolutionsByProfileId(String profileId);

}
