package com.beck.crawler.service;

import com.beck.crawler.component.CrawlWorker;
import com.beck.crawler.component.PageDataContainer;
import com.beck.crawler.config.CrawlerConfig;
import com.beck.crawler.exception.CrawlTaskInterruptedException;
import com.beck.crawler.model.PageData;
import com.beck.crawler.model.rtmap.CrawlRequest;
import com.beck.crawler.model.rtmap.CrawlResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CrawlerService {

  private final CrawlerConfig config;

  private final ExecutorService executorService;

  private final PageDataContainer pageDataContainer;

  public CrawlerService(CrawlerConfig config, PageDataContainer pageDataContainer) {
    this.config = config;
    this.pageDataContainer = pageDataContainer;
    this.executorService = Executors.newFixedThreadPool(10);
  }

  public CrawlResponse crawl(CrawlRequest request) {

    var queryId = createQueryId();

    var crawlWorkers = getCrawlFutures(request);

    var resultMap = extracted(crawlWorkers);

    pageDataContainer.put(queryId, resultMap);

    return createResponse(queryId, resultMap);
  }

  private Map<String, PageData> extracted(Map<String, Future<Map<String, PageData>>> crawlWorkers) {
    Map<String, PageData> crawlResultMap = new HashMap<>();
    for (var entity : crawlWorkers.entrySet()) {
      var oneResult = getExecutionTaskResult(entity.getValue());
      crawlResultMap.putAll(oneResult);
    }
    return crawlResultMap;
  }

  private Map<String, Future<Map<String, PageData>>> getCrawlFutures(CrawlRequest request) {
    Map<String, Future<Map<String, PageData>>> crawlWorkers = new HashMap<>();
    CountDownLatch latch = new CountDownLatch(request.getUrls().size());

    for (String url : request.getUrls()) {
      crawlWorkers.put(url, executorService.submit(new CrawlWorker(url, config, latch)));
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
        stateMap.put(entity.getKey(), "crawl failed");
      } else {
        stateMap.put(entity.getKey(), "crawl success");
      }
    }
    return CrawlResponse.builder().queryId(queryId).stateMap(stateMap).build();
  }

  private String createQueryId() {
    return UUID.randomUUID().toString();
  }

}
