package ch.epfl.favo.common;

import java.util.concurrent.CompletableFuture;

public interface DatabaseUpdater<T> {
  void addDocument(T document);

  CompletableFuture<T> getDocument(String key);
}
