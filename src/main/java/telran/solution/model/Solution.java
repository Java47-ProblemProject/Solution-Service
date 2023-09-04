package telran.solution.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "Solutions")
public class Solution {
    @Id
    protected String id;
    @Setter
    protected String author;
    @Setter
    protected String authorId;
    @Setter
    protected String problemId;
    @Setter
    protected String title;
    @Setter
    protected String details;
    protected LocalDateTime dateCreated;
    @Setter
    protected boolean checked;
    protected Reactions reactions;
    protected String type;
    public Solution(){
        this.checked = false;
        this.dateCreated = LocalDateTime.now();
        this.reactions = new Reactions();
        this.type = "SOLUTION";
    }
}
