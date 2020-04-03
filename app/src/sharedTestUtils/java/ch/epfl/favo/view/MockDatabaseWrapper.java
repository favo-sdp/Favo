package ch.epfl.favo.view;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.Document;

public class MockDatabaseWrapper<T extends Document> implements DatabaseUpdater<T> {

  T mockDocument;

  MockDatabaseWrapper(T mockDocument) {
    this.mockDocument = mockDocument;
  }

  @Override
  public void addDocument(T favor) {}

  @Override
  public void updateDocument(String key, Map<String, Object> updates) {}

  @Override
  public void removeDocument(String key) {}

  @Override
  public CompletableFuture<T> getDocument(String key) {
    CompletableFuture<T> future = new CompletableFuture<>();
    CompletableFuture.supplyAsync(() -> mockDocument);
    future.complete(mockDocument);
    return future;
  }
}
