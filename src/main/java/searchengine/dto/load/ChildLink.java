package searchengine.dto.load;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.PageDB;
import searchengine.repository.PageRepository;
import searchengine.model.SiteDB;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class ChildLink extends RecursiveAction {
    private SiteDB site;
    private String pageUrl;
    private String content;
    private String template;
    private PageRepository pageRepository;
    private long start = 0;
    final int TIME_LIMIT = 1000;

    public ChildLink(SiteDB site, String pageUrl, PageRepository pageRepository) {
        this.site = site;
        this.pageUrl  = pageUrl;
        this.pageRepository = pageRepository;

        String tmp = pageUrl;
        int i = tmp.lastIndexOf('.');
        int pos = tmp.lastIndexOf('.', i - 1);
        this.template = tmp.substring(pos + 1);

        if (this.start == 0) {
            this.start = System.currentTimeMillis();
        }
    }

    @Override
    protected void compute() {
        if ((System.currentTimeMillis() - start) < TIME_LIMIT) {

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<ChildLink> taskList = new ArrayList<>();
            Document document = null;

            try {
                    document = Jsoup.connect(pageUrl).get();

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
                        page.setPath(pageUrl);
                        page.setSite(site);
                        pageRepository.save(page);
                    } finally {
                        ;
                    }
                    Elements elements = document.select("a[href]");
                    for (Element element : elements) {
                        String url = element.attr("abs:href");
                        if (url.contains(template) && !url.contains("#") && (url.startsWith("http")) && (!url.endsWith(".pdf"))) {
//                            System.out.println(url);

                            List<PageDB> list = pageRepository.findByPath(url);
                            if (list.size() == 0) {

                                ChildLink task = new ChildLink(site, url, pageRepository);

                                task.fork();
                                taskList.add(task);
                            }
                        }
                    }
                    for (ChildLink task : taskList) {
                        task.join();
                    }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
