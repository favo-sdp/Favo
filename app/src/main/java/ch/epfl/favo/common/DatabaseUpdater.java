package ch.epfl.favo.common;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DatabaseUpdater<T> {

  CompletableFuture addDocument(T document) throws RuntimeException;

  CompletableFuture updateDocument(String key, Map<String, Object> updates);

  void removeDocument(String key);

  CompletableFuture<T> getDocument(String key);
  Query getDocumentsWithQuery(Map<String,Object> keyValuePairs);
  DocumentReference getDocumentQuery(String key);
}
