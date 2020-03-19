package ch.epfl.favo.common;

import java.util.Map;

public class CollectionWrapper {

  private String collectionReference;

  public CollectionWrapper(String collectionReference) {
    this.collectionReference = collectionReference;
  }

  public void addDocument(String key, Map document) throws RuntimeException {
    DatabaseWrapper.addDocument(key, document, collectionReference);
  }

  public void removeDocument(String key) throws RuntimeException {
    DatabaseWrapper.removeDocument(key, collectionReference);
  }

  public void updateDocument(String key, Map<String, Object> updates) throws RuntimeException {
    DatabaseWrapper.updateDocument(key, updates, collectionReference);
  }

  public void getDocument(String key, DocumentCallback callback) throws RuntimeException {
    DatabaseWrapper.getDocument(key, collectionReference, callback);
  }

    public void getAllDocuments(MultipleDocumentsCallback callback) throws RuntimeException {
        DatabaseWrapper.getAllDocuments(collectionReference, callback);
    }
}
