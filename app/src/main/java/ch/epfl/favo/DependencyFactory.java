package ch.epfl.favo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class DependencyFactory {

  private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
  private static boolean variableSet = false;

  static FirebaseUser getCurrentFirebaseUser() {
    if (variableSet) {
      return currentUser;
    }
    return FirebaseAuth.getInstance().getCurrentUser();
  }

  static void setCurrentFirebaseUser(FirebaseUser dependency) {
    variableSet = true;
    DependencyFactory.currentUser = dependency;
  }
}
