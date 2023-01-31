package searchengine.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends CrudRepository<SiteDB, Integer> {
    SiteDB findByUrl(String url);
    Optional<SiteDB> findById(Integer id);
    List<SiteDB> findByStatus(IndexStatus status);
    List<SiteDB> findByStatusNot(IndexStatus status);
}
