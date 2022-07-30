package com.beck.crawler.model;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageData {

  private String host;

  private String title;

  private Set<String> content;

}
