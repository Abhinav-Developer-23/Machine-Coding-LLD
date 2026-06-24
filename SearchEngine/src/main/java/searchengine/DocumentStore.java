package searchengine;

import java.util.HashMap;
import java.util.Map;
import searchengine.entities.Document;

public class DocumentStore {
  private final Map<String, Document> store = new HashMap<>();

  public void addDocument(Document doc) {
    store.put(doc.getId(), doc);
  }

  public Document getDocument(String docId) {
    return store.get(docId);
  }
}
