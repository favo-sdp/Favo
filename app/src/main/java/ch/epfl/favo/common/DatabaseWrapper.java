package ch.epfl.favo.common;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseWrapper {

  private static DatabaseWrapper INSTANCE = null;
  private FirebaseFirestore firestore;

  // final fields regarding ID generation
  private static String ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int ID_LENGTH = 25;

  private DatabaseWrapper() {
    FirebaseFirestore.setLoggingEnabled(true);
    FirebaseFirestoreSettings settings =
        new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
    try {
      firestore = FirebaseFirestore.getInstance();
      firestore.setFirestoreSettings(settings);
    } catch (Exception e) {
      throw e;
    }
  }

  public static DatabaseWrapper getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DatabaseWrapper();
    }
    return INSTANCE;
  }

  public static String generateRandomId() {

    StringBuilder sb = new StringBuilder(ID_LENGTH);
    for (int i = 0; i < ID_LENGTH; i++) {
      int index = (int) (36 * Math.random());
      sb.append(ID_CHARS.charAt(index));
    }
    return sb.toString();
  }

  public static void addDocument(String key, Map document, String collection)
      throws RuntimeException {
    getCollectionReference(collection)
        .document(key)
        .set(document)
        .addOnSuccessListener(aVoid -> {})
        .addOnFailureListener(
            e -> {
              throw new RuntimeException(e);
            });
  }

  public static void removeDocument(String key, String collection) throws RuntimeException {
    getCollectionReference(collection)
        .document(key)
        .delete()
        .addOnSuccessListener(aVoid -> {})
        .addOnFailureListener(
            e -> {
              throw new RuntimeException(e);
            });
  }

  public static void updateDocument(String key, Map<String, Object> updates, String collection)
      throws RuntimeException {
    getCollectionReference(collection)
        .document(key)
        .update(updates)
        .addOnSuccessListener(aVoid -> {})
        .addOnFailureListener(
            e -> {
              throw new RuntimeException(e);
            });
  }

  public static void getDocument(String key, String collection, DocumentCallback callback)
      throws RuntimeException {
    getCollectionReference(collection)
        .document(key)
        .get()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                  callback.onCallback(documentSnapshot.getData());
                } else {
                  throw new RuntimeException(String.format("Document %s does not exist ", key));
                }
              } else {
                throw new RuntimeException(task.getException());
              }
            });
  }

  public static void getAllDocuments(String collection, MultipleDocumentsCallback callback)
      throws RuntimeException {
    getCollectionReference(collection)
        .get()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                List<Map> values = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                  values.add(documentSnapshot.getData());
                }
                callback.onCallback(values);
              } else {
                throw new RuntimeException(task.getException());
              }
            });
  }

  private static CollectionReference getCollectionReference(String collection) {
    return getInstance().firestore.collection(collection);
  }
}
