package telran.solution.dto.problems;

import lombok.*;
import telran.solution.model.Reactions;

import java.time.LocalDateTime;
import java.util.Set;


@Getter

public class ProblemDto {
    protected String id;
    protected String author;
    protected Integer rating;

    protected String title;

    protected Set<String> communityNames;

    protected String details;
    protected LocalDateTime dateCreated;
    protected Double currentAward;
    protected Reactions reactions;

    protected Set<DonationDto> donationHistory;

    protected Set<String> comments;

    protected Set<String> solutions;

    protected Set<String> subscribers;

}
