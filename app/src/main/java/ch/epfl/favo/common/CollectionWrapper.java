package ch.epfl.favo.common;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CollectionWrapper<T extends Document> implements DatabaseUpdater<T> {

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

  public void removeDocument(String key) {
    DatabaseWrapper.removeDocument(key, collection);
  }

  public CompletableFuture updateDocument(String key, Map<String, Object> updates) {
    return DatabaseWrapper.updateDocument(key, updates, collection);
  }

  @Override
  public CompletableFuture<T> getDocument(String key) {
    return DatabaseWrapper.getDocument(key, cls, collection);
  }

  public CompletableFuture<List<T>> getAllDocuments() {
    return DatabaseWrapper.getAllDocuments(cls, collection);
  }
  public Query getDocumentsWithQuery(Map<String,Object> keyValuePairs){
    return DatabaseWrapper.getDocumentsWithQuery(keyValuePairs,collection);
  }
  public DocumentReference getDocumentQuery(String key){
    return DatabaseWrapper.getDocumentQuery(key,collection);
  }
}
