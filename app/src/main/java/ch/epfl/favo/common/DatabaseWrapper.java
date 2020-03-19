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
            Log.d(TAG, e.toString());
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
            int index = (int)(36 * Math.random());
            sb.append(ID_CHARS.charAt(index));
        }
        return sb.toString();
    }

    public static void addDocument(String key, Map document, String collection) throws RuntimeException {
    DatabaseWrapper.getInstance().firestore
            .collection(collection)
            .document(key)
            .set(document)
            .addOnSuccessListener( aVoid -> {})
            .addOnFailureListener( e -> { throw new RuntimeException(e); });
    }
}
