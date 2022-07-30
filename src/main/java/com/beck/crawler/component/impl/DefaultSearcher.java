package com.beck.crawler.component.impl;

import com.beck.crawler.component.ISearcher;
import org.springframework.stereotype.Component;

@Component
public class DefaultSearcher implements ISearcher {

  @Override
  public boolean search(String value, String queryText) {
    return value.contains(queryText);
  }
}
