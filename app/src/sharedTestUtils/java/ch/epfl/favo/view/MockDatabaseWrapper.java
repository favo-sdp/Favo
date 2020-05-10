package ch.epfl.favo.view;

import android.location.Location;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.database.Document;
import ch.epfl.favo.database.ICollectionWrapper;

public class MockDatabaseWrapper<T extends Document> implements ICollectionWrapper<T> {

  private T mockDocument;
  private CompletableFuture mockResult;
  private boolean throwError = false;
  private DocumentReference mockDocumentReference;

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

  public void setMockResult(T document) {
    this.mockResult =
        new CompletableFuture() {
          {
            complete(document);
          }
        };
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
  public CompletableFuture removeDocument(String key) {
    return new CompletableFuture<Void>() {
      {
        complete(null);
      }
    };
  }

  @Override
  public CompletableFuture<T> getDocument(String key) {

    CompletableFuture<T> future = new CompletableFuture<>();
    if (throwError) future.completeExceptionally(new RuntimeException());
    else future.complete(mockDocument);
    return future;
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
