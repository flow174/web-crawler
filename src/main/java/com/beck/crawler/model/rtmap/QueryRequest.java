package com.beck.crawler.model.rtmap;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QueryRequest {

  @NotEmpty(message = "query id cannot be empty")
  private String queryId;

  @NotEmpty(message = "search text cannot be empty")
  private String searchText;

}
