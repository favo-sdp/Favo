package ch.epfl.favo.common;

import java.util.Map;

public class CollectionWrapper {

  private String collectionReference;

  public CollectionWrapper(String collectionReference) {
    this.collectionReference = collectionReference;
  }

  public void addDocument(String key, Map document) {
    DatabaseWrapper.addDocument(key, document, collectionReference);
  }

  public void removeDocument(String key) {
    DatabaseWrapper.removeDocument(key, collectionReference);
  }

  public void updateDocument(String key, Map<String, Object> updates) {
    DatabaseWrapper.updateDocument(key, updates, collectionReference);
  }

  public void getDocument(String key) {
    DatabaseWrapper.getDocument(key, collectionReference);
  }

    public void getAllDocuments() {
        DatabaseWrapper.getAllDocuments(collectionReference);
    }
}
