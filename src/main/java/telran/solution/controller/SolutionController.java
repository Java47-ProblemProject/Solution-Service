package telran.solution.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telran.solution.dto.solutions.CreateSolutionDto;
import telran.solution.dto.solutions.EditSolutionDto;
import telran.solution.service.SolutionService;
import telran.solution.dto.solutions.SolutionDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/solution")

public class SolutionController {
    private final SolutionService solutionService;

    @PostMapping("/createsolution")
    public SolutionDto addSolution(@RequestBody CreateSolutionDto solution){
        return solutionService.addSolution(solution);
    }
    @PutMapping("/editsolution/{userId}/{solutionId}")
    public SolutionDto editSolution(@RequestBody EditSolutionDto solution, @PathVariable String userId, @PathVariable String solutionId) {
        return solutionService.editSolution(solution, userId,solutionId);
    }
    @DeleteMapping("/deletesolution/{solutionId}")
    public SolutionDto deleteSolution(@PathVariable String solutionId){
        return solutionService.deleteSolution(solutionId);
    }
    @PutMapping("/likesolution/{solutionId}")
    public boolean likeSolution(@PathVariable String solutionId){
        return solutionService.addLike(solutionId);
    }
    @PutMapping("/dislikesolution/{solutionId}")
    public boolean dislikeSolution(@PathVariable String solutionId){
        return solutionService.addDisLike(solutionId);
    }
    @GetMapping("/getsolution/{solutionId}")
    public SolutionDto getSolution(@PathVariable String solutionId){
        return solutionService.findSolutionById(solutionId);
    }
    @GetMapping("/getsolutions")
    public List<SolutionDto> getSolutions(){
        return solutionService.getSolutions();
    }
}
