package ch.epfl.favo.view;

import android.location.Location;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.Document;

public class MockDatabaseWrapper<T extends Document> implements DatabaseUpdater<T> {

  private T mockDocument;
  private CompletableFuture mockResult;
  private Query mockQuery;
  private boolean throwError = false;

  public MockDatabaseWrapper() {}

  public void setThrowError(boolean throwError) {
    this.throwError = throwError;
    if (throwError) {
      this.mockResult =
          new CompletableFuture() {
            {
              completeExceptionally(new RuntimeException());
            }
          };
    } else {
      this.mockResult =
          new CompletableFuture() {
            {
              complete(null);
            }
          };
    }
  }

  public void setMockDocument(T document) {
    this.mockDocument = document;
  }

  public void setMockResult(CompletableFuture result) {
    this.mockResult = result;
  }

  public void setMockQueryResult(Query query) {
    this.mockQuery = query;
  }

  @Override
  public CompletableFuture addDocument(T favor) {
    return mockResult;
  }

  @Override
  public CompletableFuture updateDocument(String key, Map<String, Object> updates) {
    return this.mockResult;
  }

  @Override
  public void removeDocument(String key) {}

  @Override
  public CompletableFuture<T> getDocument(String key) {

    CompletableFuture<T> future = new CompletableFuture<>();
    if (throwError) future.completeExceptionally(new RuntimeException());
    else future.complete(mockDocument);
    return future;
  }

  @Override
  public Query getDocumentsWithQuery(Map<String, Object> keyValuePairs) {
    return null;
  }

  @Override
  public DocumentReference getDocumentQuery(String key) {
    return null;
  }

  @Override
  public Query locationBoundQuery(Location loc, double radius) {
    return null;
  }

  public CompletableFuture<List<T>> getAllDocumentsLongitudeBounded(Location loc, double radius) {
    CompletableFuture<List<T>> future = new CompletableFuture<>();
    if (this.throwError) {
      future.completeExceptionally(new RuntimeException("Error db"));
    } else {
      future.complete((List<T>) FakeItemFactory.getFavorList());
    }
    return future;
  }
}
