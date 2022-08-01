package com.beck.crawler.component;

import java.util.Set;
import org.jsoup.nodes.Document;

public interface IParser {

  Set<String> parse(Document document);

}
