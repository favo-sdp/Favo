package ch.epfl.favo.util;

import android.content.Context;

import static ch.epfl.favo.cache.LocalCache.getValueFromCacheBool;
import static ch.epfl.favo.cache.LocalCache.getValueFromCacheStr;

class UserSettings {

  static Boolean getPreferenceNotifNew(Context context) {
    return getValueFromCacheBool(context, "notifications_new");
  }

  static String getNotificationRadius(Context context) {
    if (getPreferenceNotifNew(context)) return getValueFromCacheStr(context, "radius");
    return "disabled";
  }

  static Boolean getPreferenceNotifChat(Context context) {
    return getValueFromCacheBool(context, "notifications_chat");
  }

  static Boolean getPreferenceNotifUpdate(Context context) {
    return getValueFromCacheBool(context, "notifications_update");
  }

  static Boolean getPreferenceNotifDelete(Context context) {
    return getValueFromCacheBool(context, "notifications_delete");
  }

  static Boolean getPreferenceNotifJoin(Context context) {
    return getValueFromCacheBool(context, "notifications_join");
  }
}
