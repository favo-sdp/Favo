package ch.epfl.favo.view.tabs;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import ch.epfl.favo.R;
import ch.epfl.favo.cache.CacheUtil;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.DependencyFactory;

public class FragmentSettings extends PreferenceFragmentCompat {

  private User currentUser;
  private Preference nearbyFavorsPreference;
  private Preference radiusPreference;
  private Preference updatePreference;
  private Preference chatPreference;

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.root_preferences, rootKey);

    nearbyFavorsPreference =
        findPreference(getString(R.string.nearbyFavors_notifications_setting_key));
    radiusPreference = findPreference(getString(R.string.radius_notifications_setting_key));
    updatePreference = findPreference(getString(R.string.update_notifications_setting_key));
    chatPreference = findPreference(getString(R.string.chat_notifications_setting_key));

    displayUserPreferences(new User(null, "", "", null, null, null));

    DependencyFactory.getCurrentUserRepository()
        .findUser(DependencyFactory.getCurrentFirebaseUser().getUid())
        .thenAccept(
            user -> {
              currentUser = user;
              displayUserPreferences(user);
            });

    setPreferenceListeners();
  }

  private void setPreferenceListeners() {

    nearbyFavorsPreference.setOnPreferenceChangeListener(
        (preference, newValue) -> {
          boolean isEnabled = (boolean) newValue;

          double newRadius = 0.0;
          if (isEnabled) {
            newRadius =
                Double.parseDouble(
                    CacheUtil.getInstance()
                        .getValueFromCacheStr(
                            requireContext(),
                            getString(R.string.radius_notifications_setting_key)));
          }

          currentUser.setNotificationRadius(newRadius);
          DependencyFactory.getCurrentUserRepository().updateUser(currentUser);
          return true;
        });

    radiusPreference.setOnPreferenceChangeListener(
        (preference, newValue) -> {
          currentUser.setNotificationRadius(Double.parseDouble((String) newValue));
          DependencyFactory.getCurrentUserRepository().updateUser(currentUser);
          return true;
        });

    updatePreference.setOnPreferenceChangeListener(
        (preference, newValue) -> {
          currentUser.setUpdateNotifications((boolean) newValue);
          DependencyFactory.getCurrentUserRepository().updateUser(currentUser);
          return true;
        });

    chatPreference.setOnPreferenceChangeListener(
        (preference, newValue) -> {
          currentUser.setChatNotifications((boolean) newValue);
          DependencyFactory.getCurrentUserRepository().updateUser(currentUser);
          return true;
        });
  }

  private void displayUserPreferences(User user) {
    nearbyFavorsPreference.setDefaultValue(user.getNotificationRadius() != 0.0);
    radiusPreference.setDefaultValue(
        user.getNotificationRadius() != 0.0 ? user.getNotificationRadius() : 10);
    updatePreference.setDefaultValue(user.isUpdateNotifications());
    chatPreference.setDefaultValue(user.isChatNotifications());
  }
}
