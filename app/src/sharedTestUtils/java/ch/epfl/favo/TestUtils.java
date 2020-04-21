package ch.epfl.favo;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

import ch.epfl.favo.util.DependencyFactory;

public class TestUtils {

  public static void cleanupDatabase() throws ExecutionException, InterruptedException {
    cleanupFavorsCollection();
    cleanupChatsCollection();
  }

  private static void cleanupChatsCollection() throws ExecutionException, InterruptedException {
    Tasks.await(
            DependencyFactory.getCurrentFirestore()
                .collection("chats")
                .whereEqualTo("uid", TestConstants.USER_ID)
                .get())
        .getDocuments()
        .forEach(
            documentSnapshot ->
                documentSnapshot
                    .getReference()
                    .delete()
                    .addOnSuccessListener(
                        aVoid -> Log.d("ChatPageTests", "DocumentSnapshot successfully deleted!"))
                    .addOnFailureListener(
                        e -> {
                          Log.e("ChatPageTests", "Error deleting document", e);
                        }));
  }

  public static void cleanupFavorsCollection() throws ExecutionException, InterruptedException {
    Tasks.await(
            DependencyFactory.getCurrentFirestore()
                .collection("favors")
                .whereArrayContains("userIds", TestConstants.USER_ID)
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
