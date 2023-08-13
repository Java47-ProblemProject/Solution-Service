package telran.solution.dto.solutions;

import lombok.Getter;
import telran.solution.model.Reactions;

import java.time.LocalDateTime;

@Getter
public class SolutionDto {
    protected String id;
    protected String author;
    protected String authorId;
    protected String details;
    protected LocalDateTime dateCreated;
    protected Reactions reactions;

}