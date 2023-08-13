package telran.solution.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import telran.solution.model.Solution;

import java.util.Set;

@Repository
public interface SolutionRepository extends MongoRepository<Solution,String> {
    Set<Solution> findAllByAuthorIsNotNull();
}
