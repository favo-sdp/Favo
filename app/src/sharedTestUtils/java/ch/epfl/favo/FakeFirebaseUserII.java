package ch.epfl.favo;

import android.net.Uri;

import androidx.annotation.NonNull;

public class FakeFirebaseUserII extends FakeFirebaseUser {

  private String uid;

  public FakeFirebaseUserII(String uid) {
    super("NAME", "EMAIL", Uri.parse("PHOTO"), "PROVIDER");
    this.uid = uid;
  }

  @Override
  @NonNull
  public String getUid() {
    return uid;
  }

}
