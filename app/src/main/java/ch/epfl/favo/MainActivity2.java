package ch.epfl.favo;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;


import static androidx.navigation.Navigation.findNavController;
import static ch.epfl.favo.R.id.drawer_layout;
// import static ch.epfl.favo.R.id.toolbar;


/**
 * This view will control all the fragments that are created.
 * Contains a navigation drawer on the left.
 * Contains a bottom navigation for top-level activities.
 */
public class MainActivity2 extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  // UI
  private AppBarConfiguration appBarConfiguration;
  private NavController navController;
  private NavigationView nav;
  private DrawerLayout drawerLayout;
  private ImageButton hambMenu;
  //  //private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);

    // Initialize Variables
    nav = findViewById(R.id.nav_view);
    drawerLayout = (DrawerLayout) findViewById(drawer_layout);
    hambMenu = (ImageButton) findViewById(R.id.hamburger_menu_button);

    // Setup Controllers
    setUpHamburgerMenuButton();
    setupNavController();
    setupDrawerNavigation();
    setupBottomNavigation();

    // toolbar = findViewById(R.id.toolbar);
    // setSupportActionBar(toolbar);
    // Use tabs.

  }

  private void setUpHamburgerMenuButton() {
    final ImageButton hambMenu = (ImageButton) findViewById(R.id.hamburger_menu_button);
    hambMenu.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            drawerLayout.openDrawer(GravityCompat.START);
          }
        });
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

    //Only pass top-level destinations.
    appBarConfiguration = new AppBarConfiguration.Builder(R.id.map, R.id.fragment_favor).build();

    // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    nav.setNavigationItemSelectedListener(this);
  }
  // TODO: Implement tests

  /**
   * Will control drawer layout.
   * @param item One of the buttons in the left drawer menu.
   * @return boolean of whether operation was successful.
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

  /** Will control the bottom navigation tabs */
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
