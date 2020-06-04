package ch.epfl.favo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.Objects;

import ch.epfl.favo.notifications.FirebaseMessagingService;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.FragmentAbout;
import ch.epfl.favo.view.tabs.FragmentSettings;
import ch.epfl.favo.view.tabs.UserAccountPage;
import ch.epfl.favo.view.tabs.shop.ShopPage;

/**
 * This view will control all the fragments that are created. Contains a navigation drawer on the
 * left. Contains a bottom navigation for top-level activities.
 */
public class MainActivity extends AppCompatActivity {

  private static final String DEEP_LINK_QUERY_PARAMETER = "favorId";
  public static String GOOGLE_API_KEY;

  private static final int[] NAVIGATION_ITEMS =
      new int[] {
        R.id.nav_map,
        R.id.nav_favorList,
        R.id.nav_account,
        R.id.nav_shop,
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

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);

    setUpDeepLink();

    setContentView(R.layout.activity_main);
    GOOGLE_API_KEY = getString(R.string.google_api_key);

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
  }

  private void setUpDeepLink() {
    FirebaseDynamicLinks.getInstance()
        .getDynamicLink(getIntent())
        .addOnSuccessListener(
            this,
            pendingDynamicLinkData -> {
              if (pendingDynamicLinkData != null) {
                Uri deepLink = pendingDynamicLinkData.getLink();

                if (deepLink != null) {
                  String favorId = deepLink.getQueryParameter(DEEP_LINK_QUERY_PARAMETER);

                  if (favorId != null && !favorId.equals("")) {
                    Bundle favorBundle = new Bundle();
                    favorBundle.putString(CommonTools.FAVOR_ARGS, favorId);
                    navController.navigate(R.id.action_global_favorPublishedView, favorBundle);
                  }
                }
              }
            });
  }

  private void setupActivity() {
    // prevent swipe to open the navigation menu
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    setSupportActionBar(findViewById(R.id.toolbar_main_activity));

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
          CommonTools.hideToolBar(findViewById(R.id.toolbar_main_activity));
          switch (destination.getId()) {
            case R.id.nav_account:
            case R.id.nav_about:
            case R.id.nav_settings:
            case R.id.favorPublishedView:
            case R.id.favorEditingView:
            case R.id.nav_shop:
              if (bottomNavigationView.getVisibility() != View.GONE) {
                hideBottomNavigation();
              }
              break;

            default:
              if (bottomNavigationView.getVisibility() != View.VISIBLE) {
                showBottomNavigation();
              }
          }
          currentMenuItem = destination.getId();
        });
  }

  public void hideBottomNavigation() {
    bottomNavigationView.setVisibility(View.GONE);
    findViewById(R.id.floatingActionButton).setVisibility(View.GONE);
  }

  public void showBottomNavigation() {
    bottomNavigationView.setVisibility(View.VISIBLE);
    findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
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
              startShareIntent(getString(R.string.app_site));
              drawerLayout.closeDrawer(GravityCompat.START);
              return false;
            default:
              navController.navigate(itemId);
          }

          if (itemId == R.id.nav_shop) {
            findViewById(R.id.current_balance_text).setVisibility(View.VISIBLE);
          } else {
            findViewById(R.id.current_balance_text).setVisibility(View.GONE);
          }

          // currentMenuItem = itemId;
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

          if (itemId == R.id.nav_map) navController.popBackStack(R.id.nav_map, false);
          else navController.navigate(R.id.nav_favorList);

          // currentMenuItem = itemId;
          return false;
        });
  }

  private void showNoConnectionSnackbar() {
    Snackbar snack =
        Snackbar.make(
            findViewById(android.R.id.content).getRootView(),
            R.string.no_connection_message,
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

  public void startShareIntent(String link) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_TEXT, link);

    startActivity(Intent.createChooser(intent, getText(R.string.app_name_extended)));
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Bundle extras = intent.getExtras();
    if (extras != null) {
      Bundle favorBundle = new Bundle();
      String favorId = extras.getString(FirebaseMessagingService.FAVOR_NOTIFICATION_KEY);

      if (favorId != null && !favorId.equals("")) {
        favorBundle.putString(CommonTools.FAVOR_ARGS, favorId);

        navController.navigate(R.id.action_global_favorPublishedView, favorBundle);
      }
    }
  }

  @Override
  public void onBackPressed() {
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
          || f instanceof FragmentSettings
          || f instanceof ShopPage) {
        navController.popBackStack(R.id.nav_map, false);
        findViewById(R.id.current_balance_text).setVisibility(View.GONE);
        currentMenuItem = R.id.nav_map;
      } else {
        super.onBackPressed();
      }
    }
  }

  @Override
  public boolean onSupportNavigateUp() {

    // close keyboard if open
    CommonTools.hideSoftKeyboard(this);

    return NavigationUI.navigateUp(navController, appBarConfiguration)
        || super.onSupportNavigateUp();
  }

  public void onFabClick(View view) {
    navController.navigate(R.id.action_global_favorEditingView);
  }

  @Override
  public void onResume() {
    super.onResume();
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
  }
}
