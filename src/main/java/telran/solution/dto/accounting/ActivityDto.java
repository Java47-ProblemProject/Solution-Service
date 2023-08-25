package telran.solution.dto.accounting;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ActivityDto {
    @Setter
    protected String type;
    @Setter
    protected Boolean liked;
    @Setter
    protected Boolean disliked;
}