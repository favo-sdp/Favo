package ch.epfl.favo.database;

import android.location.Location;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DatabaseUpdater<T> {

  CompletableFuture addDocument(T document) throws RuntimeException;

  CompletableFuture updateDocument(String key, Map<String, Object> updates);

  void removeDocument(String key);

  CompletableFuture<T> getDocument(String key);

  DocumentReference getDocumentQuery(String key);

  Query locationBoundQuery(Location loc, double radius);

  CompletableFuture<List<T>> getAllDocumentsLongitudeBounded(Location loc, double radius);
}
