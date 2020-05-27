package ch.epfl.favo.util;

import android.content.Context;

import ch.epfl.favo.cache.CacheUtil;

public class UserSettings {

  public static Boolean getPreferenceNotifNew(Context context) {
    return CacheUtil.getInstance().getValueFromCacheBool(context, "notifications_new");
  }

  public static String getNotificationRadius(Context context) {
    if (getPreferenceNotifNew(context))
      return CacheUtil.getInstance().getValueFromCacheStr(context, "radius");
    return "disabled";
  }

  public static String getMapStyle(Context context) {
    if (getPreferenceNotifNew(context))
      return CacheUtil.getInstance().getValueFromCacheStr(context, "map_style");
    return "disabled";
  }

  public static Boolean getPreferenceNotifChat(Context context) {
    return CacheUtil.getInstance().getValueFromCacheBool(context, "notifications_chat");
  }

  public static Boolean getPreferenceNotifUpdate(Context context) {
    return CacheUtil.getInstance().getValueFromCacheBool(context, "notifications_update");
  }

  public static Boolean getPreferenceNotifDelete(Context context) {
    return CacheUtil.getInstance().getValueFromCacheBool(context, "notifications_delete");
  }

  public static Boolean getPreferenceNotifJoin(Context context) {
    return CacheUtil.getInstance().getValueFromCacheBool(context, "notifications_join");
  }
}
