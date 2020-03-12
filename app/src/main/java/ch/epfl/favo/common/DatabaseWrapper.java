package ch.epfl.favo.common;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Map;
import java.util.Random;

public class DatabaseWrapper {

  private static final String TAG = "DatabaseWrapper";
  private static DatabaseWrapper INSTANCE = null;
  private FirebaseFirestore firestore;

  private DatabaseWrapper() {
    FirebaseFirestore.setLoggingEnabled(true);
    FirebaseFirestoreSettings settings =
            new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
    try {
      firestore = FirebaseFirestore.getInstance();
    } catch (Exception e) {
      Log.d(TAG, e.toString());
    }
    firestore.setFirestoreSettings(settings);
  }

  public static DatabaseWrapper getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DatabaseWrapper();
    }
    return INSTANCE;
  }

  private static String generateRandomId() {
    return new Random()
            .ints(97, 123)
            .limit(20)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
  }

  public static void addDocument(Map document, String collection) throws RuntimeException {
    String documentId = DatabaseWrapper.generateRandomId();
    DatabaseWrapper.getInstance().firestore
            .collection(collection)
            .document(documentId)
            .set(document)
            .addOnSuccessListener(
                    aVoid -> {
                      Log.d(TAG, String.format("Successfully wrote document", documentId));
                    })
            .addOnFailureListener(
                    e -> {
                      throw new RuntimeException(e);
                    });
  }
}