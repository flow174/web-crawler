package com.beck.crawler.component;

import static com.beck.crawler.common.Constant.ATTRIBUTE_KEY_HREF;
import static com.beck.crawler.common.Constant.ATTRIBUTE_KEY_STYLE;
import static com.beck.crawler.common.Constant.ATTRIBUTE_VALUE_DISPLAY_NONE;
import static com.beck.crawler.common.Constant.USER_AGENTS;
import static com.beck.crawler.common.Constant.TAG_NAME_A;
import static com.beck.crawler.common.Constant.TAG_NAME_P;
import static com.beck.crawler.common.Constant.TAG_NAME_SCRIPT;

import com.beck.crawler.config.CrawlerConfig;
import com.beck.crawler.model.PageData;
import com.beck.crawler.utils.CrawlUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
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

    publishTaskFinish();

    return resultMap;

  }

  private void process(String url, int deep) {
    log.info("start process url: {} {}", deep, url);

    // get web site content
    Document document = getDocument(url);
    if (Objects.isNull(document)) {
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
    Elements elements = document.getElementsByTag(TAG_NAME_A);
    for (Element element : elements) {
      String link = element.attr(ATTRIBUTE_KEY_HREF);
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
    if (skipSpecialElement(element)) {
      return;
    }

    if (element.tag().getName().equals(TAG_NAME_P)) {
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

  private boolean skipSpecialElement(Element element) {
    return element.attr(ATTRIBUTE_KEY_STYLE).contains(ATTRIBUTE_VALUE_DISPLAY_NONE)
        || element.tag().getName().equals(TAG_NAME_SCRIPT);
  }

  private Map<String, String> createRandomHeader() {
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("User-Agent", getRandomUserAgent());
    headerMap.put("Accept-Language", "en-US,en;q=0.9");
    headerMap.put("Cache-Control", "no-cache");
    headerMap.put("Pragma", "no-cache");
    headerMap.put("DNT", "1");
    headerMap.put("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");

    return headerMap;
  }

  private String getRandomUserAgent() {
    Random random = new Random();
    int n = random.nextInt(USER_AGENTS.length);
    return USER_AGENTS[n];
  }

  private void publishTaskFinish() {
    latch.countDown();
  }
}
