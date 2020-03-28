package ch.epfl.favo.common;

import java.util.concurrent.CompletableFuture;

public interface DatabaseUpdater<T> {
  void addDocument(T document);
  void updateDocument(T document);
  void removeDocument(String key);

  CompletableFuture<T> getDocument(String key);
}
