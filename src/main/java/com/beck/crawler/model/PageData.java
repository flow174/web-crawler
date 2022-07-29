package com.beck.crawler.model;

import java.util.Set;
import lombok.Data;

@Data
public class PageData {

  private String host;

  private String title;

  private Set<String> content;

}
