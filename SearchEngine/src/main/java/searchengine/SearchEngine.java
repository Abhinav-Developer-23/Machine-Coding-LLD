package searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import searchengine.entities.Document;
import searchengine.entities.InvertedIndex;
import searchengine.entities.Posting;
import searchengine.entities.SearchResult;
import lombok.Setter;
import searchengine.strategies.ranking.RankingStrategy;
import searchengine.strategies.scoring.ScoringStrategy;

public class SearchEngine {
  private static SearchEngine instance;
  private final InvertedIndex invertedIndex;
  private final DocumentStore documentStore;

  @Setter private ScoringStrategy scoringStrategy;
  @Setter private RankingStrategy rankingStrategy;

  private SearchEngine() {
    this.invertedIndex = new InvertedIndex();
    this.documentStore = new DocumentStore();
  }

  public static synchronized SearchEngine getInstance() {
    if (instance == null) {
      instance = new SearchEngine();
    }
    return instance;
  }

  public void indexDocuments(List<Document> documents) {
    for (Document doc : documents) {
      indexDocument(doc);
    }
  }

  public void indexDocument(Document doc) {
    documentStore.addDocument(doc);

    String text = (doc.getTitle() + " " + doc.getContent()).toLowerCase();
    // Split on non-word characters (\W = not a letter, digit, or underscore) to get individual words.
    // Examples: whitespace (spaces, tabs), punctuation (. , ; : ! ?), symbols (@ # $ % &),
    // separators (- .), etc. e.g. "Python vs. Java" -> ["Python", "vs", "Java"]
    String[] tokens = text.split("\\W+");

    Map<String, Integer> termFrequency = new HashMap<>();
    for (String token : tokens) {
      if (token.isEmpty()) {
        continue;
      }
      int count = termFrequency.getOrDefault(token, 0);
      termFrequency.put(token, count + 1);
    }

    for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
      invertedIndex.add(entry.getKey(), doc.getId(), entry.getValue());
    }
  }

  public List<SearchResult> search(String query) {
    String term = query.toLowerCase();
    List<Posting> postings = invertedIndex.getPostings(term);

    List<SearchResult> results = new ArrayList<>();
    for (Posting posting : postings) {
      Document document = documentStore.getDocument(posting.getDocumentId());
      if (document != null) {
        double score = scoringStrategy.calculateScore(term, posting, document);
        results.add(new SearchResult(document, score));
      }
    }

    rankingStrategy.rank(results);
    return results;
  }
}
