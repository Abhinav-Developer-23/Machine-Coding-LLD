package searchengine.entities;

import lombok.Getter;

@Getter
public class Document {
  private final String id;
  private final String title;
  private final String content;

  public Document(String id, String title, String content) {
    this.id = id;
    this.title = title;
    this.content = content;
  }

  @Override
  public String toString() {
    return "Document(id=" + id + ", title='" + title + "')";
  }
}
