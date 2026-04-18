package org.example.strategy;

import java.util.List;

public interface UrlExtractor {

  List<String> extractUrls(String url);
}
