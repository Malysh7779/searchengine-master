package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.load.LoadPageResponse;
import searchengine.model.*;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadPageServiceImpl implements LoadPageService {

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;

    @Override
    public LoadPageResponse pageLoad(String url) {
        LoadPageResponse loadPageResponse = new LoadPageResponse();
        List<SiteDB> sites = (List<SiteDB>) siteRepository.findAll();
        for (SiteDB site : sites) {
            String content;
            String searchString = site.getUrl();
            int pos = searchString.lastIndexOf('.', searchString.lastIndexOf('.') - 1);
            searchString = searchString.substring(pos + 1);

            System.out.println(searchString);

            if (url.contains(searchString)) {
                try {
                    Document document = Jsoup.connect(url).get();

                    PageDB page = new PageDB();
                    page.setCode(document.connection().response().statusCode());
                    document.outputSettings().outline(true);

                    try {
                        content = document.text().replaceAll("[^А-Яа-я\\s]", "").replaceAll(" +", " ");
                    } catch (Exception e) {
                        content = " ";
                    }

                    try {
                        page.setContent(content);
                        page.setPath(url);
                        page.setSite(site);
                        pageRepository.save(page);
                    } finally {
                        ;
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                loadPageResponse.setResult(true);
                loadPageResponse.setStatus("Cтраница добавлена");
                return loadPageResponse;
            }
        }
        loadPageResponse.setResult(false);
        loadPageResponse.setStatus("Данная страница за пределами индексируемых сайтов");
        return loadPageResponse;
    }
}
