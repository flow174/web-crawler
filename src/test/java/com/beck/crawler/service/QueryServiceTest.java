package com.beck.crawler.service;

import com.beck.crawler.component.PageDataContainer;
import com.beck.crawler.exception.NoSuchQueryIdException;
import com.beck.crawler.model.PageData;
import com.beck.crawler.model.rtmap.QueryRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class QueryServiceTest {

  private QueryService queryService;

  @BeforeAll
  void init() {

    String queryId = "query1";

    Set<String> content1 = new HashSet<>();
    content1.add("China");
    content1.add("name");
    content1.add("Beck");
    content1.add("My name is Beck.");
    PageData pageData1 = PageData.builder().host("www.google.com").title("google").content(content1).build();

    Set<String> content2 = new HashSet<>();
    content2.add("Java");
    content2.add("Typescript");
    content2.add("I am Beck, I love Java.");
    PageData pageData2 = PageData.builder().host("www.javatpoint.com").title("java point").content(content2).build();

    Map<String, PageData> pageDataMap = new HashMap<>();
    pageDataMap.put("https://www.google.com", pageData1);
    pageDataMap.put("https://www.javatpoint.com", pageData2);

    PageDataContainer pageDataContainer = new PageDataContainer();
    pageDataContainer.put(queryId, pageDataMap);

    this.queryService = new QueryService(pageDataContainer);
  }

  @Test
  @DisplayName("query content with happy pass")
  void case1() {
    QueryRequest queryRequest = new QueryRequest();
    queryRequest.setQueryId("query1");
    queryRequest.setSearchText("Beck");
    var result = this.queryService.query(queryRequest);

    Assertions.assertEquals(3, result.getQueryResultMap().size());
  }

  @Test
  @DisplayName("query content with nothing")
  void case2() {
    QueryRequest queryRequest = new QueryRequest();
    queryRequest.setQueryId("query1");
    queryRequest.setSearchText("Daniel");
    var result = this.queryService.query(queryRequest);

    Assertions.assertEquals(0, result.getQueryResultMap().size());
  }

  @Test
  @DisplayName("no such query id exception")
  void case3() {
    QueryRequest queryRequest = new QueryRequest();
    queryRequest.setQueryId("query2");
    queryRequest.setSearchText("Beck");
    Assertions.assertThrows(NoSuchQueryIdException.class, () -> this.queryService.query(queryRequest));
  }

}