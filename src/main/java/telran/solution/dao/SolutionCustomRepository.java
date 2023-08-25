package telran.solution.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import telran.solution.model.Solution;

@Repository
@RequiredArgsConstructor
public class SolutionCustomRepository {
    private final MongoTemplate mongoTemplate;
    public void changeAuthorName(String profileId, String newName) {
        Query query = new Query(Criteria.where("authorId").is(profileId));
        Update update = new Update().set("author", newName);
        mongoTemplate.updateMulti(query, update, Solution.class);
    }
    public void deleteSolutionsByProblemId(String problemId) {
        Query query = new Query(Criteria.where("problemId").is(problemId));
        mongoTemplate.remove(query, Solution.class);
    }

    public void deleteCommentsByAuthorId(String authorId) {
        Query query = new Query(Criteria.where("authorId").is(authorId));
        mongoTemplate.remove(query, Solution.class);
    }
}
