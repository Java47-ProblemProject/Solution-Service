package telran.solution.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Reactions {
    protected Integer likes = 0;
    protected Integer dislikes = 0;

    public void addLike(){
        likes++;
    }
    public void addDislike(){
        dislikes++;
    }

    public int getTotalLikes() {
        return likes + dislikes;
    }

    public void subtractLike() {
        if (likes > 0) {
            likes--;
        }
    }
}


