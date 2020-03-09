package ch.epfl.favo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

import ch.epfl.favo.view.TabAdapter;

/**
 * This will control the general view of our app. It will contain 3 tabs. On the first tab it will
 * have the map and the favor request pop-up. On the second tab it will contain the list view of
 * previous favors. On the third tab it will contain account information. These tabs will be
 * implemented in more detail in the other presenter classes.
 */
public class MainActivity extends AppCompatActivity {

  // UI
  TabLayout tabLayout;
  ViewPager2 viewPager;
  TabAdapter myAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // retrieve current registration token for notifications
    retrieveCurrentRegistrationToken();

    // Use tabs.
    setUpViewPager();
  }

  /**
   * This function will set up the view pager and sync it with the tab layout to allow the user to
   * scroll from one tab to the other while changing the current view.
   */
  private void setUpViewPager() {
    // Instantiate UI elements
    tabLayout = findViewById(R.id.tab_layout);
    viewPager = findViewById(R.id.pager);

    // set pager orientation
    viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    myAdapter = new TabAdapter(getSupportFragmentManager(), getLifecycle());

    // hook adapter to view pager
    viewPager.setAdapter(myAdapter);
    // viewPager.setPageTransformer(new MarginPageTransformer(1500));

    // Create mediator
    // Assign icons to tabs
    new TabLayoutMediator(
            tabLayout,
            viewPager,
            (tab, position) -> {
              switch (position) {
                case 0:
                  {
                    tab.setIcon(R.drawable.ic_group_work_24px);
                    break;
                  }
                case 1:
                  {
                    tab.setIcon(R.drawable.ic_list_24px);
                    break;
                  }
                case 2:
                  {
                    tab.setIcon(R.drawable.ic_face_24px);
                    break;
                  }
              }
            })
        .attach();
  }

  private void retrieveCurrentRegistrationToken() {
    FirebaseInstanceId.getInstance()
        .getInstanceId()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                return;
              }

              // Get new Instance ID token
              String token = Objects.requireNonNull(task.getResult()).getToken();

              String msg = getString(R.string.msg_token_fmt, token);
              Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            });
  }
}
