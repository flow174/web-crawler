package com.beck.crawler.model.rtmap;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrawlResponse {

  private String queryId;

  private Map<String, String> stateMap;

}
