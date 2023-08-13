package telran.solution.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Getter
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
    protected String title;
    @Setter
    protected String details;
    protected LocalDateTime dateCreated;
    protected Reactions reactions;

    public Solution(){
        this.reactions = new Reactions(0,0);
    }

}
