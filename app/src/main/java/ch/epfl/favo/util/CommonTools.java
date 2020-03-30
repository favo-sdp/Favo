package ch.epfl.favo.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonTools {
  public static void showSnackbar(View view, String errorMessageRes) {
    Snackbar.make(view, errorMessageRes, Snackbar.LENGTH_LONG).show();
  }

  public static void replaceFragment(
      int id, FragmentManager fragmentManager, Fragment newFragment) {
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(id, newFragment);
    transaction.addToBackStack(null);
    transaction.commit();
    // transaction.remove(this);
  }

  public static String convertTime(long time) {
    Date date = new Date(time);
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    return df.format(date);
  }

  public static String convertDateToString(Date date) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    return df.format(date);
  }

  @SuppressLint("DefaultLocale")
  public static String convertLocationToString(Location location) {
    return String.format("latitude: %s longitude: %s",
            String.format("%.4f", location.getLatitude()),
            String.format("%.4f", location.getLongitude()));
  }

  public static void hideKeyboardFrom(Context context, View view) {
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    assert imm != null;
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }
}
