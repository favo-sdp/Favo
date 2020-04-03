package ch.epfl.favo.view;

import android.location.Location;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.favor.Favor;

public class MockDatabaseWrapper implements DatabaseUpdater<Favor> {
  @Override
  public void addDocument(Favor favor) {}

  @Override
  public void updateDocument(Favor document) {

  }

  @Override
  public void removeDocument(String key) {

  }

  @Override
  public CompletableFuture<Favor> getDocument(String key) {
    CompletableFuture<Favor> future = new CompletableFuture<>();
    Favor mockFavor = FakeItemFactory.getFavor();
    CompletableFuture.supplyAsync(() -> mockFavor);
    future.complete(mockFavor);
    return future;
  }
}
