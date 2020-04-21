package ch.epfl.favo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.FragmentAbout;
import ch.epfl.favo.view.tabs.FragmentSettings;
import ch.epfl.favo.view.tabs.UserAccountPage;

/**
 * This view will control all the fragments that are created. Contains a navigation drawer on the
 * left. Contains a bottom navigation for top-level activities.
 */
public class MainActivity extends AppCompatActivity {

  private static final int[] NAVIGATION_ITEMS =
      new int[] {
        R.id.nav_map,
        R.id.nav_favorList,
        R.id.nav_account,
        R.id.nav_settings,
        R.id.nav_about,
        R.id.nav_share
      };

  private NavController navController;
  private AppBarConfiguration appBarConfiguration;

  private DrawerLayout drawerLayout;
  private NavigationView navigationView;
  private BottomNavigationView bottomNavigationView;

  private int currentMenuItem;

  public Map<String, Favor> activeFavors;
  public Map<String, Favor> otherActiveFavorsAround;
  public Map<String, Favor> archivedFavors;
  public Favor focusedFavor = null;


  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize Variables
    navigationView = findViewById(R.id.nav_view);
    bottomNavigationView = findViewById(R.id.bottom_navigation_view);
    drawerLayout = findViewById(R.id.drawer_layout);
    currentMenuItem = R.id.nav_map;

    setupActivity();

    // check connection
    if (DependencyFactory.isOfflineMode(this)) {
      showNoConnectionSnackbar();
    }

    activeFavors = new HashMap<>();
    archivedFavors = new HashMap<>();
    otherActiveFavorsAround = new HashMap<>();
  }

  private void setupActivity() {
    // prevent swipe to open the navigation menu
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    setSupportActionBar(findViewById(R.id.toolbar));

    // remove title
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

    // setup app bar
    appBarConfiguration =
        new AppBarConfiguration.Builder(NAVIGATION_ITEMS).setDrawerLayout(drawerLayout).build();

    navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    setupNavController();

    navController.addOnDestinationChangedListener(
        (controller, destination, arguments) -> {
          switch (destination.getId()) {
            case R.id.nav_account:
            case R.id.nav_about:
            case R.id.nav_settings:
            case R.id.favorDetailView:
            case R.id.favorRequestView:
              if (bottomNavigationView.getVisibility() != View.GONE) {
                hideBottomNavigation();
              }
              break;

            default:
              if (bottomNavigationView.getVisibility() != View.VISIBLE) {
                showBottomNavigation();
              }
          }
        });
  }

  public void hideBottomNavigation() {
    bottomNavigationView.setVisibility(View.GONE);
    findViewById(R.id.floatingActionButton).setVisibility(View.GONE);
  }

  public void showBottomNavigation() {
    bottomNavigationView.setVisibility(View.VISIBLE);
    findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
    // NavigationUI.setupWithNavController(bottomNavigationView, navController);
  }

  private void setupNavController() {
    NavigationUI.setupWithNavController(navigationView, navController);
    NavigationUI.setupWithNavController(bottomNavigationView, navController);
    navigationView.setNavigationItemSelectedListener(
        item -> {
          int itemId = item.getItemId();
          if (itemId == currentMenuItem) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
          }

          switch (itemId) {
            case R.id.nav_map:
              navController.popBackStack(R.id.nav_map, false);
              break;
            case R.id.nav_share:
              startShareIntent();
              drawerLayout.closeDrawer(GravityCompat.START);
              return false;
            default:
              navController.navigate(itemId);
          }

          currentMenuItem = itemId;
          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
        });
    bottomNavigationView.setOnNavigationItemSelectedListener(
            item -> {
              int itemId = item.getItemId();
              if (itemId == currentMenuItem) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
              }

              if(itemId == R.id.nav_map)
                navController.popBackStack(R.id.nav_map, false);
              else navController.navigate(R.id.nav_favorList);

              currentMenuItem = itemId;
              return false;
            });
  }

  private void showNoConnectionSnackbar() {
    Snackbar snack =
        Snackbar.make(
            findViewById(android.R.id.content).getRootView(),
            "No internet connection",
            Snackbar.LENGTH_LONG);
    View view = snack.getView();
    snack.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
    params.gravity = Gravity.TOP;
    params.setMargins(
        params.leftMargin, params.topMargin + 60, params.rightMargin, params.bottomMargin);
    view.setLayoutParams(params);
    snack.show();
  }

  private void startShareIntent() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");

    shareIntent.putExtra(Intent.EXTRA_TITLE, "Favo app");
    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_site));

    startActivity(Intent.createChooser(shareIntent, null));
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Bundle extras = intent.getExtras();
    if (extras != null) {
      String favor_id = extras.getString("FavorId");
      CompletableFuture<Favor> favorFuture = FavorUtil.getSingleInstance().retrieveFavor(favor_id);

      favorFuture.thenAccept(
          favor -> {
            // otherActiveFavorsAround.put(favor.getId(), favor);
            Bundle favorBundle = new Bundle();
            favorBundle.putParcelable("FAVOR_ARGS", favor);
            navController.navigate(R.id.action_global_favorDetailView, favorBundle);
          });
    }
  }

  @Override
  public void onBackPressed() {
//    if (onBackPressedListener != null) onBackPressedListener.doBack();
//    else {
      DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
      if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        mDrawerLayout.closeDrawer(GravityCompat.START);
      else {
        NavHostFragment host =
            (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Fragment f =
            Objects.requireNonNull(host)
                .getChildFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (f instanceof UserAccountPage
            || f instanceof FragmentAbout
            || f instanceof FragmentSettings) {
          navController.popBackStack(R.id.nav_map, false);
          currentMenuItem = R.id.nav_map;
        } else {
          super.onBackPressed();
        }
      }
    //}
  }

  @Override
  public boolean onSupportNavigateUp() {
    return NavigationUI.navigateUp(navController, appBarConfiguration)
        || super.onSupportNavigateUp();
  }

  public void onFabClick(View view) {
    navController.navigate(R.id.action_global_favorRequestView);
  }

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    // Inflate the menu; this adds items to the action bar if it is present.
//    getMenuInflater().inflate(R.menu.options_menu, menu);
//
//    MenuItem myActionMenuItem = menu.findItem(R.id.search);
//    SearchView searchView = (SearchView) myActionMenuItem.getActionView();
//    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//      @Override
//      public boolean onQueryTextSubmit(String query) {
//        // Toast like print
//        return false;
//      }
//      @Override
//      public boolean onQueryTextChange(String s) {
//        return false;
//      }
//    });
//
//    return true;
//  }
}
