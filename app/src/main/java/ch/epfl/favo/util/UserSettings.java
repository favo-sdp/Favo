package ch.epfl.favo.util;

import android.content.Context;

import static ch.epfl.favo.cache.LocalCache.getValueFromCacheBool;
import static ch.epfl.favo.cache.LocalCache.getValueFromCacheStr;


public class UserSettings {

  public static Boolean getPreferenceNotifNew(Context context) {
    return getValueFromCacheBool(context, "notifications_new");
  }

  public static String getNotificationRadius(Context context) {
    if (getPreferenceNotifNew(context))
      return getValueFromCacheStr(context, "radius");
    return "disabled";
  }

  public static Boolean getPreferenceNotifChat(Context context) {
    return getValueFromCacheBool(context, "notifications_chat");
  }

  public static Boolean getPreferenceNotifUpate(Context context) {
    return getValueFromCacheBool(context, "notifications_update");
  }

  public static Boolean getPreferenceNotifDelete(Context context) {
    return getValueFromCacheBool(context, "notifications_delete");
  }

  public static Boolean getPreferenceNotifJoin(Context context) {
    return getValueFromCacheBool(context, "notifications_join");
  }

}
