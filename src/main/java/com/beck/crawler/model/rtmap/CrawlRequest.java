package com.beck.crawler.model.rtmap;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class CrawlRequest {

  @URL
  @NotEmpty
  private List<String> urls;

}
