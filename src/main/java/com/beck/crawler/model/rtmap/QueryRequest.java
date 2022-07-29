package com.beck.crawler.model.rtmap;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QueryRequest {

  @NotEmpty
  private String queryId;

  @NotEmpty
  private String searchText;

}
