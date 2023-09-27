package telran.solution.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

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
        ZoneId jerusalemZone = ZoneId.of("Asia/Jerusalem");
        this.dateCreated = LocalDateTime.now(ZoneOffset.UTC).atZone(jerusalemZone).toLocalDateTime();
        this.reactions = new Reactions();
        this.type = "SOLUTION";
    }
    public boolean checkSolutionResult(double problemRating){
        final double MIN_USER_RATING = 0.7;
        final double MIN_QUESTION_RATING = 0.6;
        final double MIN_POSITIVE_RATIO = 0.6;
        final double MIN_PARTICIPANTS = 10;
        int totalLikes = this.getReactions().getTotalLikes();
        int totalDislikes = this.getReactions().getTotalDislikes();
        int totalVotes = totalLikes + totalDislikes;
        // if not enough votes ->
        if (totalVotes < MIN_PARTICIPANTS) {
            return false;
        }
        // If MIN_POSITIVE_RATIO not enough ->
        double positiveRatio = (double) totalLikes / totalVotes;
        if (positiveRatio < MIN_POSITIVE_RATIO) {
            return false;
        }
        // If MIN_QUESTION_RATING not enough ->
        if (problemRating < MIN_QUESTION_RATING) {
            return false;
        }
        // If MIN_USER_RATING not enough->
        Double totalPositiveProfilesRating = getReactions().getLikes()
                .stream().mapToDouble(ProfileDetails::getProfileRating)
                .sum();
        Double totalNegativeProfilesRating = getReactions().getDislikes()
                .stream().mapToDouble(ProfileDetails::getProfileRating)
                .sum();
        double averageUserRating = (totalPositiveProfilesRating - totalNegativeProfilesRating) / totalVotes;
        this.checked = averageUserRating >= MIN_USER_RATING;
        return averageUserRating >= MIN_USER_RATING;
    }
}
