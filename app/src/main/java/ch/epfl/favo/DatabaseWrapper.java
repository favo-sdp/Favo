package ch.epfl.favo;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public final class DatabaseWrapper {

    private static FirebaseFirestore mFirestore;

    public DatabaseWrapper() {
        FirebaseFirestore.setLoggingEnabled(true);
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestore getDatabaseInstance() {
        return mFirestore;
    }

    public static void testDatabaseConnection() {

        // Connection test logic
        return;
    }
}
