package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.search.PageRank;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchRank;
import searchengine.dto.search.SearchResponse;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private SiteDB siteDB;
    @Autowired
    private SiteRepository siteDBRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private IndexRepository indexRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    private ArrayList<LemmaDB> listLemma = new ArrayList<>();
    private ArrayList<Integer> listIdPage = new ArrayList<>();
    private ArrayList<SearchRank> listRank = new ArrayList<>();
    private ArrayList<PageRank> pgRank = new ArrayList<>();

    private ArrayList<String> listResult = new ArrayList<>();
    private Integer lengthSnippet = 200;

    @Override
    public SearchResponse search(String searchString, String site, Integer offset, Integer limit) {
        listIdPage.clear();
        listLemma.clear();

        String[] words = searchString.split(" ");
        siteDB = siteDBRepository.findByUrl(site);
        SearchResponse searchResponse = new SearchResponse();

        for (String word : words) {
            LemmaDB lemmaDB = lemmaRepository.findByLemmaAndSite(word, siteDB);
            if (lemmaDB == null) {
                searchResponse.setError("не найдено");
                searchResponse.setResult(false);
                return searchResponse;
            }
            listLemma.add(lemmaDB);
        }

        sortListLemma();

        formListIdPage();

        calcAbsRank();

        sortPgRank();

        searchResponse = prepareResponse(limit);

        return searchResponse;
    }

    public String getSnippet(PageDB pageDB) {
        ArrayList<Integer> firstLemma = new ArrayList<>();
        String content = pageDB.getContent();
        String snippet = pageDB.getContent();
        String result = "";
        Integer prevPos = 0;
        firstLemma.clear();

//        System.out.println("page " + pageDB.getId());
//        System.out.println(pageDB.getContent());
//        System.out.println("length : " + pageDB.getContent().length());


        try {
            LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
            String[] words = content.split(" ");
//            System.out.println(words.length);

            for (String word : words) {
                if (word.length() > 2) {
                    List<String> wordBaseForm = luceneMorphology.getNormalForms(word.toLowerCase());
                    LemmaDB lemma = listLemma.get(0);
                    if (lemma.getLemma().equals(wordBaseForm.get(0))) {
                        int pos = content.indexOf(word);
                        content = content.substring(pos+1);
                        firstLemma.add(pos + prevPos);
                        prevPos = pos;
                    }
                } else {
                    continue;
                }
            }
//            for(Integer pos: firstLemma) {
//                System.out.println(" - " + pos);
//            }

            if (listLemma.size() == 1) {
//                System.out.println("test " + snippet.substring(firstLemma.get(0), firstLemma.get(0) + lengthSnippet));
                result = snippet.substring(firstLemma.get(0), firstLemma.get(0) + lengthSnippet);
//                System.out.println("test " + result);
            } else {
                // second lemma
                for (int pos : firstLemma) {
                    listResult.clear();
                    addCandidateResult(snippet, pos);
                }

                for (String candidate : listResult) {
                    for (LemmaDB lemma : listLemma) {
                        if (candidate.contains(lemma.getLemma())) {
                            result = candidate;
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public void addCandidateResult(String content, int pos) {
        int startPos;
        int endPos;
        int wordTerm;

        System.out.println();
        startPos = pos > lengthSnippet ? pos : 0;
        endPos = (pos + lengthSnippet) > content.length() ? content.length() : pos + lengthSnippet;
//        System.out.println("+ " + content.length() + " " + startPos + " " + endPos);
        listResult.add(content.substring(startPos, endPos));

        wordTerm = 24;
        for(int i = 0; i < 24; i++) {
            if (content.charAt(i) == ' ') {
                wordTerm = i;
            }
        }

        startPos = (pos - lengthSnippet + wordTerm) > 0  ? pos - lengthSnippet + wordTerm : 0;
        endPos = pos + wordTerm ;
//        System.out.println("+ " + content.length() + " " + startPos + " " + endPos);
        listResult.add(content.substring(startPos, endPos));
    }

    public SearchResponse prepareResponse(int limit) {
        SearchResponse searchResponse = new SearchResponse();

        if (pgRank.size() > 0) {
            ArrayList<SearchData> listData = new ArrayList<>();
            searchResponse.setCount(pgRank.size());
            searchResponse.setResult(true);

            for (int i = 0; i < pgRank.size(); i++) {
                if (i < limit) {
                    Optional<PageDB> optPage = pageRepository.findById(pgRank.get(i).getIdPage());
                    if (optPage.isPresent()) {
                        Optional<SiteDB> optSite = siteDBRepository.findById(optPage.get().getSite().getId());
                        if (optSite.isPresent()) {
                            SearchData searchData = new SearchData();
                            searchData.setSite(optSite.get().getUrl());
                            searchData.setSiteName(optSite.get().getName());
                            searchData.setUri(optPage.get().getPath());
                            searchData.setRelevance(pgRank.get(i).getRelRank());
                            searchData.setSnippet(getSnippet(optPage.get()));
                            listData.add(searchData);
                        }
                    }
                }
            }
            searchResponse.setSearchData(listData);
            return searchResponse;
        } else {
            searchResponse.setResult(false);
            searchResponse.setError("Нет данных соответствующих условиям поиска");
            return searchResponse;
        }
    }
    public void calcAbsRank() {
        pgRank.clear();
        for(int i = 0; i < listIdPage.size(); i++) {
            PageDB pageDB;
            PageRank pageRank;
            float absRank;

            Optional<PageDB> optPage = pageRepository.findById(listIdPage.get(i));
            if (optPage.isPresent()) {
                pageDB = optPage.get();
                pageRank = new PageRank(pageDB.getId());
            } else {
                continue;
            }
            absRank = 0;
            for(LemmaDB lemmaDB: listLemma) {
                Optional<IndexDB> optIndex = indexRepository.findByLemmaAndAndPage(lemmaDB, pageDB);
                if (optIndex.isPresent()) {
                    IndexDB indexDB = optIndex.get();
                    absRank = absRank + indexDB.getRank();
                    listRank.add(new SearchRank(pageDB.getId(), lemmaDB.getId(), indexDB.getRank()));
                }
            }
            pageRank.setAbsRank(absRank);
            pgRank.add(pageRank);
        }

        float maxRank = 0;
        for (int i = 0; i < pgRank.size(); i++) {
            maxRank = maxRank > pgRank.get(i).getAbsRank() ? maxRank : pgRank.get(i).getAbsRank();
        }

        for (int i = 0; i < pgRank.size(); i++) {
            pgRank.get(i).setRelRank(pgRank.get(i).getAbsRank()/maxRank);
        }
    }
    public void formListIdPage() {
        int i = 1;
        listIdPage.clear();

        for (LemmaDB lemma : listLemma) {
            System.out.println(lemma.getLemma() + " " + lemma.getFrequency());

            List<IndexDB> listIndex = indexRepository.findByLemma(lemma);

            if (i == 1) {
                for (IndexDB index : listIndex) {
                    listIdPage.add(index.getPage().getId());
                }
                i++;
            } else {
                for (int j = 0; j < listIdPage.size(); j++) {
                    int currId = listIdPage.get(j);
                    boolean isPresent = false;
                    for (IndexDB index : listIndex) {
                        if (index.getPage().getId() == currId) {
                            isPresent = true;
                        }
                    }

                    if (!isPresent) {
                        listIdPage.remove(j);
                    }
                }
            }
        }
    }
    public void sortPgRank() {
        Collections.sort(pgRank, new Comparator<PageRank>() {
            @Override
            public int compare(PageRank p1, PageRank p2) {
                Float f1 = p1.getRelRank();
                Float f2 = p2.getRelRank();
                return f2.compareTo(f1);
            }
        });
    }
    public void sortListLemma() {
        Collections.sort(listLemma, new Comparator<LemmaDB>() {
            @Override
            public int compare(LemmaDB l1, LemmaDB l2) {
                Integer f1 = l1.getFrequency();
                Integer f2 = l2.getFrequency();
                return f1.compareTo(f2);
            }
        });
    }
}
