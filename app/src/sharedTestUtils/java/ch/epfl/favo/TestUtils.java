package ch.epfl.favo;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.android.gms.tasks.Tasks;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Random;
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
                .collection(DependencyFactory.getCurrentFavorCollection())
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

  public static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup
            && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }

  public static String generateRandomString(int targetStringLength) {
    // String title = "sample_favor";
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    return new Random()
        .ints(leftLimit, rightLimit + 1)
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
