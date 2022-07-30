package com.beck.crawler.service;

import com.beck.crawler.component.ISearcher;
import com.beck.crawler.component.PageDataContainer;
import com.beck.crawler.model.CrawlerEntry;
import com.beck.crawler.model.PageData;
import com.beck.crawler.model.rtmap.QueryRequest;
import com.beck.crawler.model.rtmap.QueryResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueryService {

  private final PageDataContainer pageDataContainer;

  private final ISearcher searcher;

  public QueryService(PageDataContainer pageDataContainer, ISearcher searcher) {
    this.pageDataContainer = pageDataContainer;
    this.searcher = searcher;
  }

  // provide query function
  public QueryResponse query(QueryRequest request) {

    var pageDataMap = pageDataContainer.get(request.getQueryId());

    List<Entry<String, String>> queryResultMap = search(request.getSearchText(), pageDataMap);

    return QueryResponse.builder().queryResults(queryResultMap).build();
  }

  private List<Entry<String, String>> search(String queryText, Map<String, PageData> resultMap) {
    List<Entry<String, String>> queryResultList = new ArrayList<>();
    for (var entity : resultMap.entrySet()) {
      if (Objects.isNull(entity.getValue())) {
        continue;
      }

      queryResultList.addAll(searchInOneWebsite(queryText, entity.getKey(), entity.getValue()));
    }
    return queryResultList;
  }

  private List<Entry<String, String>> searchInOneWebsite(String queryText, String url, PageData pageData) {
    List<Entry<String, String>> queryResults = new LinkedList<>();
    for (var value : pageData.getContent()) {
      if (searcher.search(value, queryText)) {
        queryResults.add(new CrawlerEntry<>(value, url));
      }
    }
    return queryResults;
  }
}
