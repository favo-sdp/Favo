package ch.epfl.favo.util;

import androidx.annotation.VisibleForTesting;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DependencyFactory {

  private static FirebaseUser currentUser;
  private static boolean testMode = false;

  public static FirebaseUser getCurrentFirebaseUser() {
    if (testMode && currentUser != null) {
      return currentUser;
    }
    return FirebaseAuth.getInstance().getCurrentUser();
  }

  @VisibleForTesting
  public static void setCurrentFirebaseUser(FirebaseUser dependency) {
    testMode = true;
    DependencyFactory.currentUser = dependency;
  }
}
