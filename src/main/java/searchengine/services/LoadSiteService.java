package searchengine.services;

import searchengine.dto.load.LoadSiteResponse;

public interface LoadSiteService {
    LoadSiteResponse siteLoad(String rootUrl, String name);
}
