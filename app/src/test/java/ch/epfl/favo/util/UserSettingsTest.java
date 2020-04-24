package ch.epfl.favo.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static ch.epfl.favo.util.UserSettings.getNotificationRadius;
import static ch.epfl.favo.util.UserSettings.getPreferenceNotifChat;
import static ch.epfl.favo.util.UserSettings.getPreferenceNotifDelete;
import static ch.epfl.favo.util.UserSettings.getPreferenceNotifJoin;
import static ch.epfl.favo.util.UserSettings.getPreferenceNotifNew;
import static ch.epfl.favo.util.UserSettings.getPreferenceNotifUpdate;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserSettingsTest {

  @Mock Context mockContext;
  @Mock SharedPreferences mockPrefs;
  @Mock SharedPreferences.Editor mockEditor;

  @Before
  public void before() {
    new UserSettings(); // for test coverage
    Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
    Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt()).edit())
        .thenReturn(mockEditor);
    Mockito.when(mockPrefs.getBoolean("notifications_chat", false)).thenReturn(true);
    Mockito.when(mockPrefs.getBoolean("notifications_join", false)).thenReturn(true);
    Mockito.when(mockPrefs.getBoolean("notifications_update", false)).thenReturn(true);
    Mockito.when(mockPrefs.getBoolean("notifications_delete", false)).thenReturn(true);
    Mockito.when(mockPrefs.getString("radius", "")).thenReturn("10 KM");
  }

  @Test
  public void test_getPreferenceNotifNew() {
    Mockito.when(mockPrefs.getBoolean("notifications_new", false)).thenReturn(true);
    assertTrue(getPreferenceNotifNew(mockContext));
  }

  @Test
  public void test_getPreferenceNotifChat() {
    assertTrue(getPreferenceNotifChat(mockContext));
  }

  @Test
  public void test_getPreferenceNotifJoin() {
    assertTrue(getPreferenceNotifJoin(mockContext));
  }

  @Test
  public void test_getPreferenceNotifUpdate() {
    assertTrue(getPreferenceNotifUpdate(mockContext));
  }

  @Test
  public void test_getPreferenceNotifDelete() {
    assertTrue(getPreferenceNotifDelete(mockContext));
  }

  @Test
  public void test_getNotificationRadiusNotifEnabled() {
    Mockito.when(mockPrefs.getBoolean("notifications_new", false)).thenReturn(true);
    assertEquals("10 KM", getNotificationRadius(mockContext));
  }

  @Test
  public void test_getNotificationRadiusNotifDisabled() {
    Mockito.when(mockPrefs.getBoolean("notifications_new", true)).thenReturn(false);
    assertEquals("disabled", getNotificationRadius(mockContext));
  }
}
