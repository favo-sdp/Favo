package ch.epfl.favo;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.transition.Visibility;

import com.google.android.material.navigation.NavigationView;


import ch.epfl.favo.view.ViewController;

import static androidx.navigation.Navigation.findNavController;
import static ch.epfl.favo.R.id.drawer_layout;
// import static ch.epfl.favo.R.id.toolbar;


/**
 * This view will control all the fragments that are created.
 * Contains a navigation drawer on the left.
 * Contains a bottom navigation for top-level activities.
 */
public class MainActivity2 extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, ViewController {
  // UI
  private AppBarConfiguration appBarConfiguration;
  private NavController navController;
  private NavigationView nav;
  private DrawerLayout drawerLayout;
  private ImageButton hambMenuButton;
  private ImageButton backButton;

  // Bottom tabs
  private RadioButton mapButton;
  private RadioButton favListButton;
  //  //private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);

    // Initialize Variables
    nav = findViewById(R.id.nav_view);
    drawerLayout = (DrawerLayout) findViewById(drawer_layout);
    hambMenuButton = (ImageButton) findViewById(R.id.hamburger_menu_button);
    backButton = (ImageButton) findViewById(R.id.back_button);
    mapButton = (RadioButton) findViewById(R.id.nav_map_button);
    favListButton = (RadioButton) findViewById(R.id.nav_favor_list_button);

    // Setup Controllers
    setUpHamburgerMenuButton();
    setUpBackButton();
    setupNavController();
    setupDrawerNavigation();
    setupBottomNavigation();

    // toolbar = findViewById(R.id.toolbar);
    // setSupportActionBar(toolbar);
    // Use tabs.

  }

  private void setUpHamburgerMenuButton() {
    hambMenuButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            drawerLayout.openDrawer(GravityCompat.START);
          }
        });
  }
  private void setUpBackButton() {
    backButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                getSupportFragmentManager().popBackStackImmediate();
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
      case R.id.nav_home:
      {
        navController.navigate(R.id.nav_map);
        showBottomTabs();
        break;
      }
      case R.id.nav_account:
        {
          navController.navigate(R.id.nav_account);
          hideBottomTabs();
          break;
        }
      case R.id.nav_settings:
        {
          navController.navigate(R.id.nav_settings);
          hideBottomTabs();
          break;
        }
      case R.id.nav_about:
        {
          navController.navigate(R.id.nav_about);
          hideBottomTabs();
          break;
        }
      case R.id.nav_share:
        {
          navController.navigate(R.id.nav_share);
          hideBottomTabs();
          break;
        }
    }
    drawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }

  /** Will control the bottom navigation tabs */
  private void setupBottomNavigation() {
    mapButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                navController.navigate(R.id.nav_map);
              }
            });
    favListButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                navController.navigate(R.id.nav_favorlist);
              }
            });
  }
  @Override
  public void hideBottomTabs(){
    mapButton.setVisibility(View.INVISIBLE);
    favListButton.setVisibility(View.INVISIBLE);
  }
  @Override
  public void showBottomTabs(){
    mapButton.setVisibility(View.VISIBLE);
    favListButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void showBurgerIcon() {
    hambMenuButton.setVisibility(View.VISIBLE);
    backButton.setVisibility(View.INVISIBLE);

  }

  @Override
  public void showBackIcon() {
    backButton.setVisibility(View.VISIBLE);
    hambMenuButton.setVisibility(View.INVISIBLE);
  }

}
