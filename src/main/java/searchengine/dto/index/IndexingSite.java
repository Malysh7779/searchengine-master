package searchengine.dto.index;

import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.IndexingServiceImpl;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class IndexingSite {

    private SiteDB siteDB;
    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;


    public IndexingSite(SiteDB siteDB, SiteRepository siteRepository,
                                       PageRepository pageRepository,
                                       LemmaRepository lemmaRepository,
                                       IndexRepository indexRepository) {
        this.siteDB = siteDB;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }

    public IndexingResponse run()  {
        IndexingResponse indexingResponse = new IndexingResponse();

        Optional<SiteDB> optionalSiteDB = siteRepository.findById((siteDB.getId()));
        if(optionalSiteDB.isPresent()) {
            SiteDB editSiteDB = optionalSiteDB.get();
            editSiteDB.setStatus(IndexStatus.INDEXING);
            editSiteDB.setStatusTime(new Date());
            siteRepository.save(editSiteDB);

            List<Optional<PageDB>> optionalPageDB = pageRepository.findBySite(editSiteDB);
            for(int i = 0; i < optionalPageDB.size(); i++) {
                if (!IndexingServiceImpl.runingService) {
                    editSiteDB.setStatusTime(Date.from(Instant.now()));
                    editSiteDB.setStatus(IndexStatus.FAILED);
                    siteRepository.save(editSiteDB);

                    indexingResponse.setStatus("Индексация прервана");
                    indexingResponse.setResult(true);
                    return indexingResponse;
                }
                if (optionalPageDB.get(i).isPresent()) {

                    PageDB editPage = optionalPageDB.get(i).get();
                    IndexingPage indexingPage = new IndexingPage(siteDB, editPage, pageRepository, lemmaRepository, indexRepository);
                    System.out.println(editPage.getContent());
                    System.out.println("---------------------" + i + " из " + optionalPageDB.size());

                    indexingPage.run();
                }
            }
            editSiteDB.setStatusTime(Date.from(Instant.now()));
            editSiteDB.setStatus(IndexStatus.FAILED);
            siteRepository.save(editSiteDB);
        }

        indexingResponse.setStatus("Индексация выполнена");
        indexingResponse.setResult(true);
        return indexingResponse;
    }
}
