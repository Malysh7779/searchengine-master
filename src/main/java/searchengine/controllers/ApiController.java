package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.index.IndexingResponse;
import searchengine.dto.load.LoadPageResponse;
import searchengine.dto.load.LoadSiteResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private SiteRepository siteDBRepository;
    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private LemmaRepository lemmaRepository;

    @Autowired
    private IndexRepository indexRepository;

    private final StatisticsService statisticsService;
    private final LoadSiteService loadSiteService;

    private final LoadPageService loadPageService;
    private final IndexingService indexingService;

    private final SearchService searchService;

    public ApiController(StatisticsService statisticsService, LoadSiteService loadSiteService,
                         IndexingService indexingService, SearchService searchService,
                         LoadPageService loadPageService) {
        this.statisticsService = statisticsService;
        this.loadSiteService   = loadSiteService;
        this.indexingService   = indexingService;
        this.searchService     = searchService;
        this.loadPageService   = loadPageService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(String searchString, String site, int offset, int limit) {
        return ResponseEntity.ok(searchService.search(searchString, site, offset, limit));
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> indexing() {
        return ResponseEntity.ok(indexingService.startIndexing());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponse> stopIndexing() {
        return ResponseEntity.ok(indexingService.stopIndexing());
    }

    @GetMapping("/sites")
    public List<SiteDB> listSites() {
        Iterable<SiteDB> iterableSiteDB = siteDBRepository.findAll();
        ArrayList<SiteDB> sites = new ArrayList<>();
        for (SiteDB s: iterableSiteDB) {
            sites.add(s);
        }
        return sites;
    }

    @PostMapping("/indexPage")
    public ResponseEntity<LoadPageResponse> addPage(String url) {
        return ResponseEntity.ok(loadPageService.pageLoad(url));
    }

    @PostMapping("/sites")
    public ResponseEntity<LoadSiteResponse> add(String rootUrl, String name) {
        return ResponseEntity.ok(loadSiteService.siteLoad(rootUrl, name));
    }

    @GetMapping("/sites/{id}")
    public ResponseEntity getSite(@PathVariable int id) {
        Optional<SiteDB> optionalSiteDB = siteDBRepository.findById(id);
        if (!optionalSiteDB.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity(optionalSiteDB.get(), HttpStatus.OK);
    }

    @GetMapping("/sites/url")
    public int getSiteByUrl(@RequestParam String url) {
        SiteDB siteDB = siteDBRepository.findByUrl(url);
        if (siteDB == null) {
            return -1;
        }
        return siteDB.getId();
    }

    @DeleteMapping("/sites/{id}")
    public int delSite(@PathVariable int id) {
        Optional<SiteDB> optionalSiteDB = siteDBRepository.findById(id);
        if (optionalSiteDB.isPresent()) {
            siteDBRepository.deleteById(id);
        }
        return id;
    }

    @PutMapping("/sites/{id}")
    public int putSite(@PathVariable int id, String name, String url, String lastError, Date statusTime, IndexStatus status) {
        Optional<SiteDB> optionalSiteDB = siteDBRepository.findById(id);
        //TODO
        if (optionalSiteDB.isPresent()) {
            if (name != null)       { optionalSiteDB.get().setName(name); }
            if (lastError != null)  { optionalSiteDB.get().setLastError(lastError); }
            if (status != null)     { optionalSiteDB.get().setStatus(status); }
            if (statusTime != null) { optionalSiteDB.get().setStatusTime(statusTime); }
            if (url != null)        { optionalSiteDB.get().setUrl(url); }
            siteDBRepository.save(optionalSiteDB.get());
        }
        return id;
    }

    @GetMapping("/pages")
    public List<PageDB> listPages() {
        Iterable<PageDB> iterablePage = pageRepository.findAll();
        ArrayList<PageDB> pages = new ArrayList<>();
        for (PageDB p: iterablePage) {
            pages.add(p);
        }
        return pages;
    }

    @PostMapping("/pages")
    public int addPage(PageDB page) {
        PageDB newPage = pageRepository.save(page);
        return newPage.getId();
    }

    @GetMapping("/pages/{id}")
    public ResponseEntity getPage(@PathVariable int id) {
        Optional<PageDB> optionalPage = pageRepository.findById(id);
        if (!optionalPage.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity(optionalPage.get(), HttpStatus.OK);
    }

    @DeleteMapping("/pages/{id}")
    public int delPage(@PathVariable int id) {
        Optional<PageDB> optionalPage = pageRepository.findById(id);
        if (optionalPage.isPresent()) {
            pageRepository.deleteById(id);
        }
        return id;
    }

    @PutMapping("/pages/{id}")
    public int putPage(@PathVariable int id, SiteDB site, String path, int code, String content) {
        Optional<PageDB> optionalPage = pageRepository.findById(id);
        //TODO
        if (optionalPage.isPresent()) {
//            if (site != null)    { optionalPage.get().setSite(site); }
            if (path != null)    { optionalPage.get().setPath(path); }
            if (code != 0)       { optionalPage.get().setCode(code); }
            if (content != null) { optionalPage.get().setContent(content); }
            pageRepository.save(optionalPage.get());
        }
        return id;
    }

}
