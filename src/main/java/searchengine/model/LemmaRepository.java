package searchengine.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LemmaRepository extends CrudRepository<LemmaDB, Integer> {
    List<LemmaDB> findByLemma(String lemma);
    LemmaDB findByLemmaAndSite(String lemma, SiteDB Site);

    List<Optional<LemmaDB>> findBySite(SiteDB site);
}
