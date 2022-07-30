package com.beck.crawler.service;

import com.beck.crawler.component.PageDataContainer;
import com.beck.crawler.model.PageData;
import com.beck.crawler.model.rtmap.QueryRequest;
import com.beck.crawler.model.rtmap.QueryResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueryService {

  private final PageDataContainer pageDataContainer;

  public QueryService(PageDataContainer pageDataContainer) {
    this.pageDataContainer = pageDataContainer;
  }

  public QueryResponse query(QueryRequest request) {
    var pageDataMap = pageDataContainer.get(request.getQueryId());
    return createQueryResponse(request.getSearchText(), pageDataMap);
  }

  private QueryResponse createQueryResponse(String queryText, Map<String, PageData> resultMap) {
    Map<String, String> queryResultMap = new HashMap<>();
    if (!resultMap.isEmpty()) {
      for (var entity : resultMap.entrySet()) {
        if(Objects.isNull(entity.getValue())){
          continue;
        }
        for (var value : entity.getValue().getContent()) {
          if (value.contains(queryText)) {
            queryResultMap.put(value, entity.getKey());
          }
        }
      }
    }
    return QueryResponse.builder().queryResultMap(queryResultMap).build();
  }
}