package searchengine.entities;

import lombok.Getter;

@Getter
public class Posting {
  private final String documentId;
  private final int frequency;

  public Posting(String documentId, int frequency) {
    this.documentId = documentId;
    this.frequency = frequency;
  }
}
