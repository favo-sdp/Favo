package ch.epfl.favo.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.favo.favor.Favor;

public class CommonTools {
  public static void showSnackbar(View view, String errorMessageRes) {
    Snackbar.make(view, errorMessageRes, Snackbar.LENGTH_LONG).show();
  }

  public static String convertTime(long time) {
    Date date = new Date(time);
    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    return format.format(date);
  }

  public static void hideSoftKeyboard(Activity activity) {
    final InputMethodManager inputMethodManager =
        (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    if (inputMethodManager.isActive()) {
      if (activity.getCurrentFocus() != null) {
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
      }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  static boolean isOffline(Context context) {
    ConnectivityManager connectivity =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivity != null) {
      NetworkCapabilities network =
          connectivity.getNetworkCapabilities(connectivity.getActiveNetwork());
      return network == null
              || (!network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
              && !network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }
    return true;
  }

  public static Map<String, Favor> findFavorByTitleDescription(
      String query, Map<String, Favor> searchScope) {
    Map<String, Favor> favorsFound = new HashMap<>();
    query = query.toLowerCase();
    for (Favor favor : searchScope.values()) {
      if (favor.getTitle().toLowerCase().contains(query)
          || favor.getDescription().toLowerCase().contains(query))
        favorsFound.put(favor.getId(), favor);
    }
    return favorsFound;
  }
}
