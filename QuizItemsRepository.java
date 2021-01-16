package engine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface QuizItemsRepository extends JpaRepository<QuizItem, Integer> {
    @Query("select q from QuizItem q")
    Page<QuizItem> findAll(Pageable pageable);


}
