package engine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizCompletionsRepository extends JpaRepository<QuizCompletion,Integer> {
    @Query("select qc from QuizCompletion qc where qc.userCompleted = ?1")
    Page<QuizCompletion> findByUserCompleted(User userCompleted, Pageable pageable);
}
//where userCompleted = %?1%