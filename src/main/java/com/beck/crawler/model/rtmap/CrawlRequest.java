package com.beck.crawler.model.rtmap;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CrawlRequest {

  @NotEmpty(message = "URL cannot be empty")
  private List<String> urls;

}
