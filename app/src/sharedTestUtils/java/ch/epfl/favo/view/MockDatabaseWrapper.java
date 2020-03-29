package ch.epfl.favo.view;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.favor.Favor;

class MockDatabaseWrapper implements DatabaseUpdater<Favor> {
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
    return null;
  }
}
