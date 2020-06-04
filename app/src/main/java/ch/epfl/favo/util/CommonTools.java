package ch.epfl.favo.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionException;

import ch.epfl.favo.R;
import ch.epfl.favo.exception.IllegalAcceptException;
import ch.epfl.favo.exception.IllegalRequestException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.view.NonClickableToolbar;

@SuppressLint("NewApi")
public class CommonTools {
  public static final String FAVOR_ARGS = "FAVOR_ARGS";
  public static final String FAVOR_VALUE_ARGS = "FAVOR_VALUE_ARGS";
  public static final String USER_ARGS = "USER_ARGS";
  public static final int TEXT_MESSAGE_TYPE = 0;
  public static final int IMAGE_MESSAGE_TYPE = 1;
  public static final int LOCATION_MESSAGE_TYPE = 2;

  public static void showSnackbar(View view, String errorMessageRes) {
    Snackbar.make(view, errorMessageRes, Snackbar.LENGTH_LONG).show();
  }

  public static void hideToolBar(NonClickableToolbar toolbar) {
    toolbar.setBackgroundColor(Color.TRANSPARENT);
    toolbar.setTitleTextColor(Color.BLACK);
    Objects.requireNonNull(toolbar.getNavigationIcon())
        .setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP));
    toolbar.setTitle("");
  }

  public static String convertTime(Date time) {
    @SuppressLint("SimpleDateFormat")
    Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    return format.format(time);
  }

  public static String emailToName(String email) {
    return email.split("@")[0].replace(".", " ");
  }

  public static int notificationRadiusToZoomLevel(double radius) {
    int level;
    int r = (int) radius;
    switch (r) {
      case 1:
        level = 16;
        break;
      case 5:
        level = 14;
        break;
      case 10:
        level = 13;
        break;
      default: // 25
        level = 11;
    }
    return level;
  }

  public static void hideSoftKeyboard(Activity activity) {
    final InputMethodManager inputMethodManager =
        (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    assert inputMethodManager != null;
    if (inputMethodManager.isActive()) {
      if (activity.getCurrentFocus() != null) {
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
      }
    }
  }

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

  public static int getSnackbarMessageForRequestedFavor(Context context) {
    if (DependencyFactory.isOfflineMode(context)) {
      return R.string.save_draft_message;
    } else {
      return R.string.favor_request_success_msg;
    }
  }

  public static int getSnackbarMessageForFailedRequest(CompletionException exception) {
    if (exception.getCause() instanceof IllegalRequestException)
      return R.string.illegal_request_error;
    else return R.string.update_favor_error;
  }

  public static void handleException(
      Throwable throwable, View parentView, Context context, String TAG) {
    Throwable cause =
        (throwable.getCause() == null) ? new Exception(throwable) : throwable.getCause();
    if (cause instanceof IllegalRequestException) {
      CommonTools.showSnackbar(
          parentView, context.getResources().getString(R.string.illegal_request_error));
    } else if (cause instanceof IllegalAcceptException) {
      CommonTools.showSnackbar(
          parentView, context.getResources().getString(R.string.illegal_accept_error));
    } else {
      CommonTools.showSnackbar(
          parentView, context.getResources().getString(R.string.update_favor_error));
    }
    if (throwable.getMessage() != null) Log.e(TAG, throwable.getMessage());
  }

  /**
   * Check if a camera is available in the current used device
   *
   * @param activity: activity of rhe
   * @return: boolean indicating if a camera is available on the current device
   */
  public static boolean isCameraAvailable(Activity activity) {
    boolean hasCamera =
            activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    int numberOfCameras = 0;
    try {
      numberOfCameras =
              ((CameraManager)
                      Objects.requireNonNull(
                              activity.getSystemService(Context.CAMERA_SERVICE)))
                      .getCameraIdList()
                      .length;
    } catch (CameraAccessException e) {
      Log.e("CameraAvailability", e.toString());
    }
    return (hasCamera && numberOfCameras != 0);
  }
}
