package ch.epfl.favo;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ch.epfl.favo.view.TabAdapter;

import static androidx.navigation.Navigation.findNavController;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static androidx.navigation.ui.NavigationUI.setupWithNavController;
import static ch.epfl.favo.R.id.drawer_layout;
import static ch.epfl.favo.R.id.nav_host_fragment;
//import static ch.epfl.favo.R.id.toolbar;
import static com.google.android.gms.common.util.CollectionUtils.setOf;

/**
 * This will control the general view of our app. It will contain 3 tabs. On the first tab it will
 * have the map and the favor request pop-up. On the second tab it will contain the list view of
 * previous favors. On the third tab it will contain account information. These tabs will be
 * implemented in more detail in the other presenter classes.
 */
public class MainActivity2 extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  private AppBarConfiguration appBarConfiguration;
  private NavController navController;
  private NavigationView nav;
  private DrawerLayout drawerLayout;
  //private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);
    nav = findViewById(R.id.nav_view);
    drawerLayout = (DrawerLayout) findViewById(drawer_layout);
    //toolbar = findViewById(R.id.toolbar);
    //setSupportActionBar(toolbar);
    setupNavController();
    setupDrawerNavigation();
    setupBottomNavigation();


    // Use tabs.

  }

  private void setupNavController() {
    navController = findNavController(this, R.id.nav_host_fragment);
  }

  @Override
  public boolean onSupportNavigateUp() {
    return NavigationUI.navigateUp(navController, appBarConfiguration)
        || super.onSupportNavigateUp();
  }

  private void setupDrawerNavigation() {
    // Passing each menu ID as a set of Ids because
    // each menu should be considered as top level
    // destination

    appBarConfiguration = new AppBarConfiguration.Builder(R.id.map, R.id.fragment_favor).build();

    //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    nav.setNavigationItemSelectedListener(this);
  }
  //TODO: Implement tests
  //TODO: Figure out how to show hamburger menu
  //TODO: Constraint fragment to be over the bottom tabview

  /**
   * Will control hamburger menu
   *
   * @param item
   * @return
   */
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.nav_account:
        {
          navController.navigate(R.id.nav_account);

          break;
        }
      case R.id.nav_settings:
        {
          navController.navigate(R.id.nav_settings);

          break;
        }
      case R.id.nav_about:
        {
          navController.navigate(R.id.nav_about);

          break;
        }
      case R.id.nav_share:
        {
          navController.navigate(R.id.nav_share);

          break;
        }
    }
    drawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }

  private void setupBottomNavigation() {
    findViewById(R.id.nav_map_button)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                navController.navigate(R.id.nav_map);
              }
            });
    findViewById(R.id.nav_favor_list_button)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                navController.navigate(R.id.nav_favorlist);
              }
            });
  }


}
