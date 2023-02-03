package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.load.ChildLink;
import searchengine.dto.load.LoadSiteResponse;
import searchengine.model.IndexStatus;
import searchengine.repository.PageRepository;
import searchengine.model.SiteDB;
import searchengine.repository.SiteRepository;

import java.sql.Date;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class LoadSiteServiceImpl implements LoadSiteService {

    @Autowired
    private SiteRepository siteDBRepository;
    @Autowired
    private PageRepository pageRepository;

    @Override
    public LoadSiteResponse siteLoad (String rootUrl, String name) {
        String[] statuses = { "LOAD", "LOADED", "INDEXED", "FAILED", "INDEXING" };
        String[] errors = {
                "Ошибка загрузки: главная страница сайта не доступна",
                "Ошибка загрузки: сайт не доступен",
                ""
        };

        LoadSiteResponse loadSiteResponse = new LoadSiteResponse();

        SiteDB siteDB = siteDBRepository.findByUrl(rootUrl);

        if (siteDB != null) {
            if (siteDB.getStatus() == IndexStatus.LOAD) {
                loadSiteResponse.setStatus("LOAD");
                loadSiteResponse.setResult(false);
                return loadSiteResponse;
            } else {
                siteDBRepository.deleteById(siteDB.getId());
            }
        }

        siteDB = new SiteDB();
        siteDB.setStatus(IndexStatus.LOAD);
        siteDB.setUrl(rootUrl);
        siteDB.setStatusTime(Date.from(Instant.now()));
        siteDB.setLastError(null);
        siteDB.setName(name);
        siteDBRepository.save(siteDB);

        System.out.println("Start load site " + siteDB.getUrl() + " " + Date.from(Instant.now()));

        new ForkJoinPool().invoke(new ChildLink(siteDB, siteDB.getUrl(), pageRepository));

        System.out.println("Loaded site " + siteDB.getUrl() + " " + Date.from(Instant.now()));

        siteDB.setStatus(IndexStatus.LOADED);
        siteDB.setStatusTime(Date.from(Instant.now()));
        siteDBRepository.save(siteDB);

        loadSiteResponse.setStatus("LOADED");
        loadSiteResponse.setResult(true);
        return loadSiteResponse;
    }
}
