package ch.epfl.favo;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;

public class TestUtils {
  public static void cleanupFavorsCollection() throws ExecutionException, InterruptedException {
    Tasks.await(
            DependencyFactory.getCurrentFirestore()
                    .collection("favors-test")
                    .whereEqualTo(Favor.REQUESTER_ID, TestConstants.REQUESTER_ID)
                    .get())
            .getDocuments()
            .forEach(
                    documentSnapshot ->
                            documentSnapshot
                                    .getReference()
                                    .delete()
                                    .addOnSuccessListener(
                                            aVoid -> Log.d("FavorPageTests", "DocumentSnapshot successfully deleted!"))
                                    .addOnFailureListener(
                                            e -> {
                                              Log.e("FavorPageTests", "Error deleting document", e);
                                            }));
  }
}
