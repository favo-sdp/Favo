package ch.epfl.favo.common;

import android.location.Location;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CollectionWrapper<T extends Document> implements ICollectionWrapper<T> {

  private String collection;
  private Class cls;

  public CollectionWrapper(String collection, Class cls) {
    this.collection = collection;
    this.cls = cls;
  }

  @Override
  public CompletableFuture addDocument(T document) throws RuntimeException {
    return DatabaseWrapper.addDocument(document, collection);
  }

  @Override
  public void removeDocument(String key) {
    DatabaseWrapper.removeDocument(key, collection);
  }

  @Override
  public CompletableFuture updateDocument(String key, Map<String, Object> updates) {
    return DatabaseWrapper.updateDocument(key, updates, collection);
  }

  @Override
  public CompletableFuture<T> getDocument(String key) {
    return DatabaseWrapper.getDocument(key, cls, collection);
  }

  @Override
  public CompletableFuture<List<T>> getAllDocumentsLongitudeBounded(Location loc, double radius) {
    return DatabaseWrapper.getAllDocumentsLongitudeBounded(loc, radius, cls, collection);
  }

  public CompletableFuture<List<T>> getAllDocuments() {
    return DatabaseWrapper.getAllDocuments(cls, collection);
  }



  public DocumentReference getDocumentQuery(String key) {
    return DatabaseWrapper.getDocumentQuery(key, collection);
  }


  public Query locationBoundQuery(Location loc, double radius) {
    return DatabaseWrapper.locationBoundQuery(loc, radius, collection);
  }
}
