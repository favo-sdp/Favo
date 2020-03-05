package ch.epfl.favo.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DependencyFactory {

  private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
  private static boolean variableSet = false;

  public static FirebaseUser getCurrentFirebaseUser() {
    if (variableSet) {
      return currentUser;
    }
    return FirebaseAuth.getInstance().getCurrentUser();
  }

  public static void setCurrentFirebaseUser(FirebaseUser dependency) {
    variableSet = true;
    DependencyFactory.currentUser = dependency;
  }
}
