package ch.epfl.favo.common;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CollectionWrapper<T extends Document> {

  private String collection;
  private Class cls;

  public CollectionWrapper(String collection, Class cls) {
    this.collection = collection;
    this.cls = cls;
  }

  public void addDocument(T document) {
    DatabaseWrapper.addDocument(document, collection);
  }

  public void removeDocument(T document) {
    DatabaseWrapper.removeDocument(document, collection);
  }

  public void updateDocument(T document, Map<String, Object> updates) {
    DatabaseWrapper.updateDocument(document, updates, collection);
  }

  public CompletableFuture<T> getDocument(T document) {
    return DatabaseWrapper.getDocument(document, cls, collection);
  }

  public CompletableFuture<List<T>> getAllDocuments() {
    return DatabaseWrapper.getAllDocuments(cls, collection);
  }
}
