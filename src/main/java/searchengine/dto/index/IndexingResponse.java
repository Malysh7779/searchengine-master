package searchengine.dto.index;

import lombok.Data;
import searchengine.dto.statistics.StatisticsData;
@Data
public class IndexingResponse {
    private boolean result;
    private String status;
}
