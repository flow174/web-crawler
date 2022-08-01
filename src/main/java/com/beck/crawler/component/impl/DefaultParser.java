package com.beck.crawler.component.impl;

import static com.beck.crawler.common.Constant.ATTRIBUTE_KEY_STYLE;
import static com.beck.crawler.common.Constant.ATTRIBUTE_VALUE_DISPLAY_NONE;
import static com.beck.crawler.common.Constant.TAG_NAME_P;
import static com.beck.crawler.common.Constant.TAG_NAME_SCRIPT;

import com.beck.crawler.component.IParser;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class DefaultParser implements IParser {

  @Override
  public Set<String> parse(Document document) {
    Set<String> contents = new HashSet<>();
    for (var child : document.body().children()) {
      parseElement(child, contents);
    }
    return contents;
  }

  private void parseElement(Element element, Set<String> contents) {
    if (skipSpecialElement(element)) {
      return;
    }

    // tag p special handle
    if (element.tag().getName().equals(TAG_NAME_P)) {
      var text = element.text();
      contents.add(text);
      return;
    }

    // save the content only when element has no children
    var children = element.children();
    if (children.isEmpty()) {
      var text = element.text();
      contents.add(text);
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
}
