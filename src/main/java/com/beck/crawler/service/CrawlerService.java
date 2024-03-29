package com.beck.crawler.service;

import static com.beck.crawler.common.Constant.CRAWL_FAILED;
import static com.beck.crawler.common.Constant.CRAWL_SUCCESS;

import com.beck.crawler.component.CrawlWorker;
import com.beck.crawler.component.IParser;
import com.beck.crawler.component.PageDataContainer;
import com.beck.crawler.config.CrawlerConfig;
import com.beck.crawler.exception.CrawlTaskInterruptedException;
import com.beck.crawler.exception.InvalidURLException;
import com.beck.crawler.exception.OverLimitedURLNumberException;
import com.beck.crawler.model.PageData;
import com.beck.crawler.model.rtmap.CrawlRequest;
import com.beck.crawler.model.rtmap.CrawlResponse;
import com.beck.crawler.utils.CrawlUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CrawlerService {

  private final IParser parser;

  private final CrawlerConfig config;

  private final ExecutorService executorService;

  private final PageDataContainer pageDataContainer;

  public CrawlerService(IParser parser,
      CrawlerConfig config,
      PageDataContainer pageDataContainer,
      ExecutorService executorService) {
    this.parser = parser;
    this.config = config;
    this.pageDataContainer = pageDataContainer;
    this.executorService = executorService;
  }

  // Provide crawl function
  public CrawlResponse crawl(CrawlRequest request) {

    requestValidate(request);

    var queryId = createQueryId();
    var crawlWorkers = exercise(request);
    var resultMap = extract(crawlWorkers);

    pageDataContainer.put(queryId, resultMap);

    return createResponse(queryId, resultMap);
  }

  private Map<String, Future<Map<String, PageData>>> exercise(CrawlRequest request) {
    Map<String, Future<Map<String, PageData>>> crawlWorkers = new HashMap<>();
    CountDownLatch latch = new CountDownLatch(request.getUrls().size());

    for (var url : request.getUrls()) {
      crawlWorkers.put(url, executorService.submit(new CrawlWorker(url, parser, config, latch)));
    }

    waitingForTheCrawlToFinish(latch);

    return crawlWorkers;
  }

  private void waitingForTheCrawlToFinish(CountDownLatch latch) {
    try {
      latch.await();
    } catch (InterruptedException exception) {
      throw new CrawlTaskInterruptedException();
    }
  }

  private Map<String, PageData> extract(Map<String, Future<Map<String, PageData>>> crawlWorkers) {
    Map<String, PageData> crawlResultMap = new HashMap<>();
    for (var entity : crawlWorkers.entrySet()) {
      var oneResult = getExecutionTaskResult(entity.getValue());
      crawlResultMap.putAll(oneResult);
    }
    return crawlResultMap;
  }

  private void requestValidate(CrawlRequest request) {
    if (request.getUrls().size() > config.getMaxUrlNum()) {
      throw new OverLimitedURLNumberException(config.getMaxUrlNum());
    }

    for (var url : request.getUrls()) {
      if (!CrawlUtils.getInstance().isValidLink(url)) {
        throw new InvalidURLException(url);
      }
    }
  }

  private Map<String, PageData> getExecutionTaskResult(Future<Map<String, PageData>> future) {
    Map<String, PageData> resultMap = null;
    try {
      resultMap = future.get();
    } catch (InterruptedException | ExecutionException ex) {
      log.warn("Get task result error");
      log.error(ex.getMessage(), ex);
    }
    return resultMap;
  }

  private CrawlResponse createResponse(String queryId, Map<String, PageData> crawlResultMap) {
    Map<String, String> stateMap = new HashMap<>();
    for (var entity : crawlResultMap.entrySet()) {
      if (Objects.isNull(entity.getValue())) {
        stateMap.put(entity.getKey(), CRAWL_FAILED);
      } else {
        stateMap.put(entity.getKey(), CRAWL_SUCCESS);
      }
    }
    return CrawlResponse.builder().queryId(queryId).stateMap(stateMap).build();
  }

  private String createQueryId() {
    return UUID.randomUUID().toString();
  }

}
