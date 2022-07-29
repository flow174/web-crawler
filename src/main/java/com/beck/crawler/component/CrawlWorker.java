package com.beck.crawler.component;

import static com.beck.crawler.common.Constant.HEADERS;

import com.beck.crawler.config.CrawlerConfig;
import com.beck.crawler.exception.BaseApplicationException;
import com.beck.crawler.exception.ReadWebsiteException;
import com.beck.crawler.model.PageData;
import com.beck.crawler.utils.CrawlUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import javax.print.Doc;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Slf4j
public class CrawlWorker implements Callable<Map<String, PageData>> {

  private final String url;
  private final CrawlerConfig config;
  private final CountDownLatch latch;
  private final Map<String, PageData> resultMap;

  public CrawlWorker(String url, CrawlerConfig config, CountDownLatch latch) {
    this.url = url;
    this.config = config;
    this.latch = latch;
    this.resultMap = new HashMap<>();
  }

  @Override
  public Map<String, PageData> call() {

    int deep = config.getDeepLevel();
    process(url, deep);

    finish();

    return resultMap;

  }

  private void process(String url, int deep) {
    log.info("start process url: {} {}", deep, url);

    // get web site content
    Document document = getDocument(url);

    if(Objects.isNull(document)) {
      resultMap.put(url, null);
      return;
    }

    // process top level url
    parse(document, url);

    // process sub level url
    if (needDeepParse(deep)) {
      deepParse(document, --deep);
    }
    log.info("end process");
  }

  private boolean needDeepParse(int deep) {
    return deep > 0;
  }

  private Document getDocument(String url) {
    Document document = null;
    try {
      document = Jsoup.connect(url)
          .headers(createRandomHeader())
          .timeout(config.getTimeOutSecond() * 1000)
          .ignoreContentType(true)
          .get();
    } catch (Exception e) {
      log.warn("cannot get web sit: {}", url);
      log.error(e.getMessage(), e);
    }
    return document;
  }

  private void parse(Document document, String url) {
    PageData data = new PageData();
    data.setHost(url);
    data.setTitle(document.title());
    data.setContent(extractContentData(document));
    resultMap.put(url, data);
  }

  private void deepParse(Document document, int deep) {
    Elements elements = document.getElementsByTag("a");
    for (Element element : elements) {
      String link = element.attr("href");
      if (CrawlUtils.getInstance().isValidLink(link)) {
        if (!resultMap.containsKey(link)) {
          process(link, deep);
        }
      }
    }
  }

  private Set<String> extractContentData(Document res) {
    Set<String> contents = new HashSet<>();
    for (var child : res.body().children()) {
      parseElement(child, contents);
    }
    return contents;
  }

  private void parseElement(Element element, Set<String> contents) {
    if (needSkipSpecialElement(element)) {
      return;
    }

    if(element.tag().getName().equals("p")) {
      var text = element.text();
      if (!text.isEmpty()) {
        contents.add(text);
      }
      return;
    }

    var children = element.children();
    if (children.isEmpty()) {
      var text = element.text();
      if (!text.isEmpty()) {
        contents.add(text);
      }
    } else {
      for (var child : children) {
        parseElement(child, contents);
      }
    }
  }

  private boolean needSkipSpecialElement(Element element) {
    return element.attr("style").contains("display:none") || element.tag().getName().equals("script");
  }

  private Map<String, String> createRandomHeader() {
    Random r = new Random();
    int n = r.nextInt(HEADERS.length);

    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("User-Agent", HEADERS[n]);
    headerMap.put("Accept-Language", "en-US,en;q=0.9");
    headerMap.put("Cache-Control", "no-cache");
    headerMap.put("Pragma", "no-cache");
    headerMap.put("DNT", "1");
    headerMap.put("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");

    return headerMap;
  }

  private void finish() {
    latch.countDown();
  }
}
