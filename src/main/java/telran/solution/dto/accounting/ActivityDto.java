package telran.solution.dto.accounting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ActivityDto {
    protected String problemId;
    protected Boolean liked;
    protected Boolean disliked;
}