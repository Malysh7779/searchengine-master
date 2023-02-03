package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.index.IndexingResponse;
import searchengine.dto.index.IndexingSite;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    public static boolean runingService;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private IndexRepository indexRepository;

    @Override
    public IndexingResponse stopIndexing() {
        IndexingServiceImpl.runingService = false;
        IndexingResponse indexingResponse = new IndexingResponse();
        indexingResponse.setStatus("Индексация прервана пользователем");
        indexingResponse.setResult(false);
        return indexingResponse;
    }

    @Override
    public IndexingResponse startIndexing() {
        IndexingServiceImpl.runingService = true;

        List<SiteDB> listIndexing = siteRepository.findByStatus(IndexStatus.INDEXING);
        IndexingResponse indexingResponse = new IndexingResponse();

        if (!listIndexing.isEmpty()) {
            indexingResponse.setStatus("Индексация уже запущена");
            indexingResponse.setResult(false);
            return indexingResponse;
        }

        List<SiteDB> listNotIndexed = siteRepository.findByStatusNot(IndexStatus.INDEXING);
        if (!listNotIndexed.isEmpty()) {
            for(int i = 0; i < listNotIndexed.size(); i++) {
                indexingResponse = new IndexingSite(listNotIndexed.get(i), siteRepository, pageRepository, lemmaRepository, indexRepository).run();
            }
        }

        indexingResponse.setResult(true);
        IndexingServiceImpl.runingService = false;
        return indexingResponse;
    }
}
