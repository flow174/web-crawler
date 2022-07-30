package com.beck.crawler.model.rtmap;

import java.util.List;
import java.util.Map.Entry;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryResponse {

  List<Entry<String, String>> queryResults;

}
