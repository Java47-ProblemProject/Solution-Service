package telran.solution.service;

import telran.solution.dto.solutions.CreateSolutionDto;
import telran.solution.dto.solutions.EditSolutionDto;
import telran.solution.dto.solutions.SolutionDto;

import java.util.List;

public interface SolutionService {
    SolutionDto addSolution(CreateSolutionDto solution);

    SolutionDto editSolution(EditSolutionDto solution, String userId, String solutionId);

    SolutionDto deleteSolution(String solutionId);

    boolean addLike(String solutionId);



    boolean addDisLike(String solutionId);

    SolutionDto findSolutionById(String solutionId);

    List<SolutionDto> getSolutions();
}
