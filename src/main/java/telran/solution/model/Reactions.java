package telran.solution.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Reactions {
    protected Integer likes;
    protected Integer dislikes;

    public void addLike(){
        this.likes++;
    }
    public void addDislike() {
        this.dislikes++;
    }

    public void removeLike(){
        this.likes--;
    }
    public void removeDislike(){
        this.dislikes--;
    }
}




