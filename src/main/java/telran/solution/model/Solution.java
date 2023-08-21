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
    protected String title;
    @Setter
    protected String details;
    protected LocalDateTime dateCreated;
    protected Reactions reactions;
    protected String type;
    public Solution(){
        this.dateCreated = LocalDateTime.now();
        this.reactions = new Reactions(0,0);
        this.type = "SOLUTION";
    }
    public void setReactionsLike(){this.reactions.setLikes(this.reactions.getLikes()+1);}
    public void setReactionsDislike(){this.reactions.setDislikes(this.reactions.getDislikes()+1);}

}
