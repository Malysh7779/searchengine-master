package searchengine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.PageDB;
import searchengine.model.SiteDB;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends CrudRepository<PageDB, Integer> {
//    PageDB findByPath(String path);
    List<PageDB> findByPath(String path);
    Optional <PageDB> findById(Long id);
    List<Optional<PageDB>> findBySite(SiteDB site);

//    @Query("SELECT p1.id, p1.code, p1.content, p1.path, p1.site_id " +
//           "FROM search_engine.page p1, search_engine.lemma l1, search_engine.indx i1 " +
//           "WHERE " +
//           "    l1.lemma = ?2 " +
//           "    AND i1.page_id = p1.id " +
//           "    AND i1.lemma_id = l1.id " +
//           "    AND p1.id IN (SELECT p.id " +
//           "                  FROM search_engine.lemma l, search_engine.indx i, search_engine.page p " +
//           "                  WHERE " +
//           "                       l.lemma = ?1 " +
//           "                       AND l.id = i.lemma_id " +
//           "                       AND i.page_id = p.id)")
//    List<PageDB> findAllByLemmas(String lemma1, String lemma2);

//    @Query("SELECT p1.id, p1.code, p1.content, p1.path, p1.site_id " +
//            "FROM search_engine.page p1, search_engine.lemma l1, search_engine.indx i1 " +
//            "WHERE " +
//            "    l1.lemma = ?1 " +
//            "    AND i1.page_id = p1.id " +
//            "    AND i1.lemma_id = l1.id ")
//    List<PageDB> searchDistinctById(String lemma1);
}
