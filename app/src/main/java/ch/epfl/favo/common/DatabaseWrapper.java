package ch.epfl.favo.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.util.Log;

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

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.TaskToFutureAdapter;

@SuppressLint("NewApi")
public class DatabaseWrapper {

  private static DatabaseWrapper INSTANCE = null;
  private FirebaseFirestore firestore;

  // final fields regarding ID generation
  private static final String ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int ID_LENGTH = 28;

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

  static CompletableFuture updateDocument(String key, Map<String, Object> updates, String collection) {
    Task update = getCollectionReference(collection).document(key).update(updates);
    return new TaskToFutureAdapter<>(update).getInstance();
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

  /**
   * It is a temporary, simpler version to retrieve favors in a **square area** on sphere surface.
   * This function retrieve longitude bounded documents because firebase only supports one range query field
   * For some reason, reading from db is very slow when adding one more whereEqualTo query field
   * TODO: use firebase functions or other server code to perform customized filtering and fetch the result
   */
  static <T extends Document> CompletableFuture<List<T>> getAllDocumentsLongitudeBounded(
      Location loc, double radius, Class<T> cls, String collection) {
    double longDif = Math.toDegrees(radius / (6371 * Math.cos(Math.toRadians(loc.getLatitude()))));
    Task<QuerySnapshot> getAllTask = getCollectionReference(collection)
            .whereGreaterThan("location.longitude", loc.getLongitude() - longDif)
            .whereLessThan("location.longitude", loc.getLongitude() + longDif).limit(30).get();
    CompletableFuture<QuerySnapshot> getAllFuture = new TaskToFutureAdapter<>(getAllTask).getInstance();
    return getAllFuture.thenApply(
            querySnapshot -> querySnapshot.toObjects(cls));
  }

  private static CollectionReference getCollectionReference(String collection) {
    return getInstance().firestore.collection(collection);
  }
}
