package searchengine.dto.search;

import lombok.Data;
import java.util.ArrayList;

@Data
public class SearchResponse {
    private boolean result;
    private int count;
    private String error;
    private ArrayList<SearchData> searchData;
}
