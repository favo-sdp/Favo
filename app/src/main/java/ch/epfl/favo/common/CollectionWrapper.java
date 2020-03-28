package ch.epfl.favo.common;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CollectionWrapper<T extends Document> implements DatabaseUpdater<T> {

  private String collection;
  private Class cls;

  public CollectionWrapper(String collection, Class cls) {
    this.collection = collection;
    this.cls = cls;
  }

  @Override
  public void addDocument(T document) {
    DatabaseWrapper.addDocument(document, collection);
  }

  public void removeDocument(String key) {
    DatabaseWrapper.removeDocument(key, collection);
  }

  public void updateDocument(T document) {
    DatabaseWrapper.updateDocument(document.getId(),document.toMap(),collection);
  }

  @Override
  public CompletableFuture<T> getDocument(String key) {
    return DatabaseWrapper.getDocument(key, cls, collection);
  }

  public CompletableFuture<List<T>> getAllDocuments() {
    return DatabaseWrapper.getAllDocuments(cls, collection);
  }
}
