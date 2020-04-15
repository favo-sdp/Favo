package ch.epfl.favo.view;

import android.location.Location;

import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.Document;

public class MockDatabaseWrapper<T extends Document> implements DatabaseUpdater<T> {

  private T mockDocument;
  private CompletableFuture mockResult;
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

    if (throwError) future.completeExceptionally(new RuntimeException());
    else future.complete(mockDocument);
    return future;
  }

  @Override
  public CompletableFuture<List<T>> getAllDocumentsLongitudeBounded(Location loc, double radius) {
    CompletableFuture<List<T>> future = new CompletableFuture<>();
    if (throwError) future.completeExceptionally(new RuntimeException());
    else {
      ArrayList<T> arrayList = new ArrayList<>();
      arrayList.add(mockDocument);
      future.complete(arrayList);
    }
    return future;
  }

}
