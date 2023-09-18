package telran.solution.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import telran.solution.model.Solution;

import java.util.stream.Stream;


@Repository
public interface SolutionRepository extends MongoRepository<Solution,String> {
    Stream<Solution> findAllByAuthorId(String profileId);
    Stream<Solution> findAllByProblemId(String problemId);
}
