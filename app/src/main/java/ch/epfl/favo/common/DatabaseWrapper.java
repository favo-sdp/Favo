package ch.epfl.favo.common;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressLint("NewApi")
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

  public static void addDocument(String key, Map document, String collection) {
    new TaskToFutureAdapter<>(getCollectionReference(collection).document(key).set(document));
  }

  public static void removeDocument(String key, String collection) {
    Task<Void> deleteTask = getCollectionReference(collection).document(key).delete();
    new TaskToFutureAdapter<>(deleteTask);
  }

  public static void updateDocument(String key, Map<String, Object> updates, String collection) {
    Task<Void> updateTask = getCollectionReference(collection).document(key).update(updates);
    new TaskToFutureAdapter<>(updateTask);
  }

  public static CompletableFuture<Map> getDocument(String key, String collection) {
    Task<DocumentSnapshot> getTask = getCollectionReference(collection).document(key).get();
    CompletableFuture<DocumentSnapshot> future = new TaskToFutureAdapter<>(getTask);

    return future.thenApply(
        documentSnapshot -> {
          if (documentSnapshot.exists()) {
            return documentSnapshot.getData();
          } else {
            throw new RuntimeException(String.format("Document %s does not exist ", key));
          }
        });
  }

  public static CompletableFuture<List<Map>> getAllDocuments(String collection) {
    Task<QuerySnapshot> getTask = getCollectionReference(collection).get();
    CompletableFuture<QuerySnapshot> future = new TaskToFutureAdapter<>(getTask);

    return future.thenApply(
        querySnapshot -> {
          List<Map> values = new ArrayList<>();
          for (DocumentSnapshot documentSnapshot : querySnapshot) {
            values.add(documentSnapshot.getData());
          }
          return values;
        });
  }

  private static CollectionReference getCollectionReference(String collection) {
    return getInstance().firestore.collection(collection);
  }
}
