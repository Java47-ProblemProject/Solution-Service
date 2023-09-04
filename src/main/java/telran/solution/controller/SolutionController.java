package telran.solution.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telran.solution.dto.CreateEditSolutionDto;
import telran.solution.dto.SolutionDto;
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

    @PutMapping("/dislikesolution/{problemId}/{solutionId}")
    public Boolean addDislike(@PathVariable String problemId, @PathVariable String solutionId) {
        return solutionService.addDisLike(problemId, solutionId);
    }

    @PutMapping("/editsolution/{profileId}/{problemId}/{solutionId}")
    public SolutionDto editSolution(@PathVariable String profileId, @PathVariable String problemId, @PathVariable String solutionId, @RequestBody CreateEditSolutionDto details) {
        return solutionService.editSolution(problemId, solutionId, details);
    }

    @DeleteMapping("/deletesolution/{profileId}/{problemId}/{solutionId}")
    public SolutionDto deleteSolution(@PathVariable String profileId, @PathVariable String problemId, @PathVariable String solutionId) {
        return solutionService.deleteSolution(problemId, solutionId);
    }

    @GetMapping("/getsolution/{problemId}/{solutionId}")
    public SolutionDto getSolution(@PathVariable String problemId, @PathVariable String solutionId) {
        return solutionService.getSolution(problemId, solutionId);
    }

    @GetMapping("/getsolutions/{problemId}")
    public Set<SolutionDto> getSolutions(@PathVariable String problemId) {
        return solutionService.getSolutions(problemId);
    }

    @GetMapping("/getautorsolutions/{profileId}")
    public Set<SolutionDto> getSolutionsByProfileId(@PathVariable String profileId) {
        return solutionService.getSolutionsByProfileId(profileId);
    }
}
