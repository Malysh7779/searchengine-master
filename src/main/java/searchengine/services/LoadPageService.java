package searchengine.services;

import searchengine.dto.load.LoadPageResponse;
import searchengine.dto.load.LoadSiteResponse;

public interface LoadPageService {
    LoadPageResponse pageLoad(String url);
}
