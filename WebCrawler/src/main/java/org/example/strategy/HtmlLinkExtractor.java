package org.example.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HtmlLinkExtractor implements UrlExtractor {

  private final Map<String, List<String>> webGraph;

  public HtmlLinkExtractor(Map<String, List<String>> webGraph) {
    this.webGraph = webGraph;
  }

  @Override
  public List<String> extractUrls(String url) {
    List<String> links = webGraph.get(url);
    if (links == null) {
      return new ArrayList<>();
    }
    return links;
  }
}
