package ch.epfl.favo.common;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DatabaseUpdater<T> {

  void addDocument(T document) throws RuntimeException;

  void updateDocument(String key, Map<String, Object> updates);

  void removeDocument(String key);

  CompletableFuture<T> getDocument(String key);
}
