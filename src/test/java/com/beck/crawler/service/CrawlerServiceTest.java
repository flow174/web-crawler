package com.beck.crawler.service;

import com.beck.crawler.common.Constant;
import com.beck.crawler.component.PageDataContainer;
import com.beck.crawler.config.CrawlerConfig;
import com.beck.crawler.exception.InvalidURLException;
import com.beck.crawler.exception.OverLimitedURLNumberException;
import com.beck.crawler.model.rtmap.CrawlRequest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class CrawlerServiceTest {

  private CrawlerService crawlerService;

  @BeforeAll
  void init() {
    CrawlerConfig config = new CrawlerConfig();
    config.setDeepLevel(0);
    config.setTimeOutSecond(30);
    config.setMaxUrlNum(2);

    PageDataContainer pageDataContainer = new PageDataContainer();

    this.crawlerService = new CrawlerService(config, pageDataContainer);
  }

  @Test
  @DisplayName("Specified over limited URL")
  void case1() {
    List<String> urls = List.of("https://www.google.com", "https://www.javatpoint.com", "https://stackoverflow.blog/");
    CrawlRequest crawlRequest = new CrawlRequest();
    crawlRequest.setUrls(urls);
    Assertions.assertThrows(OverLimitedURLNumberException.class, () -> this.crawlerService.crawl(crawlRequest));
  }

  @Test
  @DisplayName("Invalid URL")
  void case2() {
    List<String> urls = List.of("stackoverflow.blog/", "https://www.javatpoint.com");
    CrawlRequest crawlRequest = new CrawlRequest();
    crawlRequest.setUrls(urls);
    Assertions.assertThrows(InvalidURLException.class, () -> this.crawlerService.crawl(crawlRequest));
  }

  @Test
  @DisplayName("Crawl a fake URL")
  void case3() {
    String fakeUrl = "https://thisisafakewebsiteformytest.com";
    List<String> urls = List.of(fakeUrl);
    CrawlRequest crawlRequest = new CrawlRequest();
    crawlRequest.setUrls(urls);
    var result = this.crawlerService.crawl(crawlRequest);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getStateMap().size());
    Assertions.assertEquals(Constant.CRAWL_FAILED, result.getStateMap().get(fakeUrl));
  }

  @Test
  @DisplayName("Crawl a URL and success")
  void case4() {
    // cannot make sure this url always valid, just an example, furthermore we can use mock
    String url = "https://stackoverflow.blog/";
    List<String> urls = List.of(url);
    CrawlRequest crawlRequest = new CrawlRequest();
    crawlRequest.setUrls(urls);
    var result = this.crawlerService.crawl(crawlRequest);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getStateMap().size());
    Assertions.assertEquals(Constant.CRAWL_SUCCESS, result.getStateMap().get(url));
  }
}