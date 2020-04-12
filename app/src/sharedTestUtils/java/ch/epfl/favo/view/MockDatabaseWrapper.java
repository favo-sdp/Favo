package ch.epfl.favo.view;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.Document;

public class MockDatabaseWrapper<T extends Document> implements DatabaseUpdater<T> {

  private T mockDocument;
  private CompletableFuture mockResult;

  public MockDatabaseWrapper() {}

  public void setMockDocument(T document) {
    this.mockDocument = document;
  }

  public void setMockResult(CompletableFuture result) {
    this.mockResult = result;
  }

  @Override
  public void addDocument(T favor) {}

  @Override
  public CompletableFuture updateDocument(String key, Map<String, Object> updates) {
    return this.mockResult;
  }

  @Override
  public void removeDocument(String key) {}

  @Override
  public CompletableFuture<T> getDocument(String key) {
    CompletableFuture<T> future = new CompletableFuture<>();
    future.complete(mockDocument);
    return future;
  }
}
