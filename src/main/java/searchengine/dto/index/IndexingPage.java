package searchengine.dto.index;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import searchengine.model.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class IndexingPage {

    private SiteDB siteDB;
    private PageDB pageDB;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;

    private HashMap<String, Integer> listWord;

    public IndexingPage(SiteDB siteDB, PageDB pageDB, PageRepository pageRepository, LemmaRepository lemmaRepository, IndexRepository indexRepository) {
        this.siteDB = siteDB;
        this.pageDB = pageDB;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.listWord = new HashMap<>();
    }

    public void run() {
        try {
            LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
            String[] words = pageDB.getContent().split(" ");

            for(String word : words) {
//                if (word.contains("профессионал")) {
//                    System.out.println(word + pageDB.getId());
//                }
                if (word.trim().length() > 2) {
                if (word.charAt(0) == '-') {
                    word = word.substring(1);
                }
                    List<String> wordBaseForm = luceneMorphology.getNormalForms(word.toLowerCase());
//                    System.out.println(word);
//                    System.out.println("---- " + wordBaseForm.get(0));
                    listWord.put(wordBaseForm.get(0), listWord.get(wordBaseForm.get(0)) == null ? 1 : listWord.get(wordBaseForm.get(0))+1);
                }
            }

            for(String key: listWord.keySet()) {
                LemmaDB lemma = lemmaRepository.findByLemmaAndSite(key, siteDB);
                if (lemma == null) {
//                    System.out.println("no");
//
                    lemma = new LemmaDB();
                    lemma.setLemma(key);
                    lemma.setFrequency(listWord.get(key));
                    lemma.setSite(pageDB.getSite());
                    lemmaRepository.save(lemma);
                } else {
//                    System.out.println("yes");
                    lemma.setFrequency(lemma.getFrequency() + listWord.get(key));
                    lemmaRepository.save(lemma);
                }

                IndexDB indexDB = new IndexDB();
                indexDB.setLemma(lemma);
                indexDB.setPage(pageDB);
                indexDB.setRank(listWord.get(key));
                indexRepository.save(indexDB);
////                System.out.println(key + " - " + listWord.get(key));
            }

        } catch (Exception e) {
            System.out.println("#" + e.getMessage());
        }

    }
}
