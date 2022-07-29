package com.beck.crawler.component;

import com.beck.crawler.exception.NoSuchQueryIdException;
import com.beck.crawler.model.PageData;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class PageDataContainer {

  @Getter
  private final Map<String, Map<String, PageData>> innerData;

  public PageDataContainer() {
    innerData = new HashMap<>();
  }

  public Map<String, PageData> get(String queryId) {
    if (innerData.containsKey(queryId)) {
      return innerData.get(queryId);
    }
    throw new NoSuchQueryIdException();
  }

  public void put(String queryId, Map<String, PageData> pageDataMap) {
    innerData.put(queryId, pageDataMap);
  }
}
