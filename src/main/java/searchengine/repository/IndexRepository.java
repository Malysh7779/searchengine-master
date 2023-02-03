package searchengine.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexDB;
import searchengine.model.LemmaDB;
import searchengine.model.PageDB;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndexRepository extends CrudRepository<IndexDB, Integer> {
    List<IndexDB> findByLemma(LemmaDB lemmaDB);
    Optional<IndexDB> findByLemmaAndAndPage(LemmaDB lemmaDB, PageDB pageDB);
}
