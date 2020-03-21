package ch.epfl.favo.common;

import java.util.Map;

public class CollectionWrapper implements DatabaseUpdater{

  private static final String TAG = "DatabaseWrapper";
  private String collectionReference;

  public CollectionWrapper(String collectionReference) {
    this.collectionReference = collectionReference;
  }

  public void addDocument(String key, Map document) {
    DatabaseWrapper.addDocument(key, document, collectionReference);
  }
}
