package searchengine.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex {
  private final Map<String, List<Posting>> index = new HashMap<>();

  public void add(String term, String documentId, int frequency) {
    List<Posting> postings = index.computeIfAbsent(term, ignored -> new ArrayList<>());
    postings.add(new Posting(documentId, frequency));
    index.put(term, postings);
  }

  public List<Posting> getPostings(String term) {
    return index.getOrDefault(term, Collections.emptyList());
  }
}
