package com.beck.crawler.controller;

import static com.beck.crawler.common.Constant.APPLICATION_JSON_UTF8_VALUE;

import com.beck.crawler.model.rtmap.CrawlRequest;
import com.beck.crawler.model.rtmap.CrawlResponse;
import com.beck.crawler.model.rtmap.QueryRequest;
import com.beck.crawler.model.rtmap.QueryResponse;
import com.beck.crawler.service.CrawlerService;
import com.beck.crawler.service.QueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "web crawler application api", tags = {"crawler"})
@RequestMapping("/v1")
@RestController
@Slf4j
public class WebCrawlerController {

  private final CrawlerService crawlerService;

  private final QueryService queryService;

  public WebCrawlerController(CrawlerService crawlerService, QueryService queryService) {
    this.crawlerService = crawlerService;
    this.queryService = queryService;
  }

  /**
   * crawl specified urls
   *
   * @param request web crawler request, include urls
   * @return web crawler status with a query id
   */
  @ApiOperation(value = "This API is used to crawl specified web site")
  @PostMapping(value = {"/crawl"}, produces = APPLICATION_JSON_UTF8_VALUE)
  public CrawlResponse craw(@RequestBody @Validated CrawlRequest request) {
    return crawlerService.crawl(request);
  }

  /**
   * query contents from crawled urls with query id
   *
   * @param request query request
   * @return contents with url
   */
  @ApiOperation(value = "This API is used to query specified key words")
  @PostMapping(value = {"/query"}, produces = APPLICATION_JSON_UTF8_VALUE)
  public QueryResponse query(@RequestBody @Validated QueryRequest request) {
    return queryService.query(request);
  }

}
