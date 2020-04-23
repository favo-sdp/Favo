package ch.epfl.favo.common;

import android.location.Location;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DatabaseUpdater<T> {

  void addDocument(T document) throws RuntimeException;

  CompletableFuture updateDocument(String key, Map<String, Object> updates);

  void removeDocument(String key);

  CompletableFuture<T> getDocument(String key);

  CompletableFuture<List<T>> getAllDocumentsLongitudeBounded(Location loc, double radius);
}
