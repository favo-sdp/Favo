package ch.epfl.favo.common;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.TaskToFutureAdapter;

@SuppressLint("NewApi")
public class DatabaseWrapper {

  private static DatabaseWrapper INSTANCE = null;
  private FirebaseFirestore firestore;

  // final fields regarding ID generation
  private static final String ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int ID_LENGTH = 25;

  private DatabaseWrapper() {
    FirebaseFirestore.setLoggingEnabled(true);
    FirebaseFirestoreSettings settings =
        new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
    try {
      firestore = DependencyFactory.getCurrentFirestore();
      firestore.setFirestoreSettings(settings);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to initialize FirebaseFirestore");
    }
  }

  private static DatabaseWrapper getInstance() {
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

  public static <T extends Document> void addDocument(T document, String collection) {
    getCollectionReference(collection).document(document.getId()).set(document);
  }

  static <T extends Document> void removeDocument(String key, String collection) {
    getCollectionReference(collection).document(key).delete();
  }

  static void updateDocument(String key, Map<String, Object> updates, String collection) {
    getCollectionReference(collection).document(key).update(updates);
  }

  static <T extends Document> CompletableFuture<T> getDocument(
      String key, Class<T> cls, String collection) throws RuntimeException {
    Task<DocumentSnapshot> getTask = getCollectionReference(collection).document(key).get();
    CompletableFuture<DocumentSnapshot> getFuture = new TaskToFutureAdapter<>(getTask).getInstance();
    return getFuture.thenApply(
        documentSnapshot -> {
          if (documentSnapshot.exists()) {
            return documentSnapshot.toObject(cls);
          } else {
            throw new RuntimeException(String.format("Document %s does not exist ", key));
          }
        });
  }

  static <T extends Document> CompletableFuture<List<T>> getAllDocuments(
      Class<T> cls, String collection) {
    Task<QuerySnapshot> getAllTask = getCollectionReference(collection).get();
    CompletableFuture<QuerySnapshot> getAllFuture = new TaskToFutureAdapter<>(getAllTask).getInstance();

    return getAllFuture.thenApply(querySnapshot -> querySnapshot.toObjects(cls));
  }

  private static CollectionReference getCollectionReference(String collection) {
    return getInstance().firestore.collection(collection);
  }
}
