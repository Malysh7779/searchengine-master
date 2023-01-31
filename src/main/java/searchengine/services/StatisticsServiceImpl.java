package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final Random random = new Random();
    private final SitesList sites;

    @Autowired
    private SiteRepository siteDBRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;


    @Override
    public StatisticsResponse getStatistics() {
        String[] statuses = { "LOAD", "LOADED", "INDEXED", "FAILED", "INDEXING" };
        String[] errors = {
                "Ошибка индексации: главная страница сайта не доступна",
                "Ошибка индексации: сайт не доступен",
                ""
        };

        replacementSites();

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(true);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sitesList = sites.getSites();
        for(int i = 0; i < sitesList.size(); i++) {
            Site site = sitesList.get(i);
            DetailedStatisticsItem item = new DetailedStatisticsItem();

            SiteDB siteDB = siteDBRepository.findByUrl(site.getUrl());
            List<Optional<PageDB>> sitePages = pageRepository.findBySite(siteDB);
            List<Optional<LemmaDB>> siteLemmas = lemmaRepository.findBySite(siteDB);

            item.setName(site.getName());
            item.setUrl(site.getUrl());

//            int pages = random.nextInt(1_000);
            int pages = sitePages.size();
//            int lemmas = pages * random.nextInt(1_000);
            int lemmas = siteLemmas.size();

            item.setPages(pages);
            item.setLemmas(lemmas);
//            item.setStatus(statuses[i % 3]);
            item.setStatus(siteDB.getStatus().toString());
//            item.setError(errors[i % 3]);
            item.setError(siteDB.getLastError());

//            item.setStatusTime(System.currentTimeMillis() - (random.nextInt(10_000)));
            item.setStatusTime(siteDB.getStatusTime().getTime());

            total.setPages(total.getPages() + pages);
            total.setLemmas(total.getLemmas() + lemmas);
            detailed.add(item);
        }

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }

    private void replacementSites() {
        sites.clear();

        List<SiteDB> list = (List<SiteDB>) siteDBRepository.findAll();
        for(SiteDB siteDB: list) {
            Site site = new Site();
            site.setName(siteDB.getName());
            site.setUrl(siteDB.getUrl());
            sites.add(site);
        }
    }
}
