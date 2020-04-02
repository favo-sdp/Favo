package ch.epfl.favo.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    return format.format(date);
  }

  public static void hideKeyboardFrom(Context context, View view) {
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static boolean isOffline(Context context) {
    ConnectivityManager connectivity =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivity != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        NetworkCapabilities network =
            connectivity.getNetworkCapabilities(connectivity.getActiveNetwork());
        if (network != null
            && (network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {
          return false;
        }
      } else {
        NetworkInfo network = connectivity.getActiveNetworkInfo();
        if (network != null
            && (network.getType() == ConnectivityManager.TYPE_WIFI
                || network.getType() == ConnectivityManager.TYPE_MOBILE)) {
          return false;
        }
      }
    }
    return true;
  }
}
