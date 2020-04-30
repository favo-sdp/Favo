package ch.epfl.favo.util;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import ch.epfl.favo.favor.Favor;

public class FavorFragmentFactory {
  public static String FAVOR_ARGS = "FAVOR_ARGS";
  public static String USER_ARGS = "USER_ARGS";

  public static Fragment instantiate(Favor favor, Fragment destination) {
    Bundle args = new Bundle();
    args.putParcelable(FAVOR_ARGS, favor);
    destination.setArguments(args);
    return destination;
  }
}
