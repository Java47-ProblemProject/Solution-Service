package telran.solution.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telran.solution.dto.solutions.CreateEditSolutionDto;
import telran.solution.dto.solutions.SolutionDto;
import telran.solution.service.SolutionService;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/solution")

public class SolutionController {
    final SolutionService solutionService;

    @PostMapping("/addsolution/{problemId}")
    public SolutionDto addSolution(@PathVariable String problemId, @RequestBody CreateEditSolutionDto details) {
        return solutionService.addSolution(problemId, details);
    }

    @PutMapping("/likesolution/{problemId}/{solutionId}")
    public Boolean addLike(@PathVariable String problemId, @PathVariable String solutionId) {
        return solutionService.addLike(problemId, solutionId);
    }

    @PutMapping("/dislikesolutioin/{problemId}/{solutionId}")
    public Boolean addDislike(@PathVariable String problemId, @PathVariable String solutionId) {
        return solutionService.addDisLike(problemId, solutionId);
    }

    @PutMapping("/editsolution/{problemId}/{solutionId}")
    public SolutionDto editSolution(@PathVariable String problemId, @PathVariable String solutionId, @PathVariable CreateEditSolutionDto details) {
        return solutionService.editSolution(problemId, solutionId, details);
    }

    @DeleteMapping("/deletesolution/{problemId}/{solutionId}")
    public SolutionDto deleteSolution(@PathVariable String problemId, @PathVariable String solutionId) {
        return solutionService.deleteSolution(problemId, solutionId);
    }

    @GetMapping("/getsolution/{problemId}/{solutionId}")
    public SolutionDto getSolution(@PathVariable String problemId, @PathVariable String solutionId) {
        return solutionService.getSolution(problemId, solutionId);
    }

    @GetMapping("/{problemId}/getsolutions")
    public Set<SolutionDto> getSolutions(@PathVariable String problemId) {
        return solutionService.getSolutions(problemId);
    }
}
