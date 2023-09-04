package telran.solution.dto;

import telran.solution.model.ProfileDetails;

import java.util.Set;

public class ReactionsDto {
    protected Integer totalLikes;
    protected Integer totalDislikes;
    protected Set<ProfileDetails> likes;
    protected Set<ProfileDetails> dislikes;
}
