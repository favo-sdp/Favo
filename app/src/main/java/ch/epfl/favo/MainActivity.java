package ch.epfl.favo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import ch.epfl.favo.util.DependencyFactory;

/**
 * This view will control all the fragments that are created. Contains a navigation drawer on the
 * left. Contains a bottom navigation for top-level activities.
 */
public class MainActivity extends AppCompatActivity {

  private NavController navController;
  private AppBarConfiguration appBarConfiguration;

  private DrawerLayout drawerLayout;
  private NavigationView navigationView;
  private BottomNavigationView bottomNavigationView;

  private boolean mKeyboardVisible = false;

  public Map<String, Favor> activeFavors;
  public Map<String, Favor> otherActiveFavorsAround;
  public Map<String, Favor> archivedFavors;

  public OnBackPressedListener onBackPressedListener;

  public interface OnBackPressedListener {
    void doBack();
  }

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

    setupActivity();

    // check connection
    if (DependencyFactory.isOfflineMode(this)) {
      showNoConnectionSnackbar();
    }

    activeFavors = new HashMap<>();
    archivedFavors = new HashMap<>();
    otherActiveFavorsAround = new HashMap<>();
  }

  @SuppressLint("ClickableViewAccessibility")
  private void setupActivity() {
    // prevent swipe to open the navigation menu
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    setSupportActionBar(findViewById(R.id.toolbar));

    // remove title
//    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

    // setup app bar
    appBarConfiguration =
        new AppBarConfiguration.Builder(
                R.id.nav_map,
                R.id.nav_favorList,
                R.id.nav_account,
                R.id.nav_settings,
                R.id.nav_about,
                R.id.nav_share)
            .setDrawerLayout(drawerLayout)
            .build();

    navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    setupNavController();

    navController.addOnDestinationChangedListener(
        (controller, destination, arguments) -> {
          switch (destination.getId()) {
            case R.id.nav_account:
            case R.id.nav_about:
            case R.id.nav_settings:
              hideBottomNavigation();
              break;

            default:
              showBothNavigation();
          }
        });
  }

  public void hideBottomNavigation() {
    findViewById(R.id.bottom_navigation_view).setVisibility(View.GONE);
  }

  public void showBothNavigation() {
    findViewById(R.id.bottom_navigation_view).setVisibility(View.VISIBLE);
    NavigationUI.setupWithNavController(bottomNavigationView, navController);
  }

  private void setupNavController() {
    NavigationUI.setupWithNavController(navigationView, navController);
    navigationView.setNavigationItemSelectedListener(
        item -> {
          int itemId = item.getItemId();

          if (itemId == R.id.nav_share) {
            startShareIntent();
          } else {
            navController.navigate(itemId);
          }

          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
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

  /**
   * This is used to hide navigation bar when input contents in search bar, and recover the
   * navigation bar when soft keyboard displays. It only works when the current view is SearchView,
   * for sake of, if possible, unnecessary slowing down.
   */
  @Override
  protected void onResume() {
    super.onResume();
    findViewById(android.R.id.content)
        .getViewTreeObserver()
        .addOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener);
  }

  @Override
  protected void onPause() {
    super.onPause();
    findViewById(android.R.id.content)
        .getViewTreeObserver()
        .removeOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener);
  }

  private final ViewTreeObserver.OnGlobalLayoutListener mLayoutKeyboardVisibilityListener =
      () -> {
        View view = getCurrentFocus();
        if (view == null || !view.toString().startsWith("android.widget.SearchView")) return;
        final Rect rectangle = new Rect();
        final View contentView = findViewById(android.R.id.content);
        contentView.getWindowVisibleDisplayFrame(rectangle);
        int screenHeight = contentView.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // If keypad is shown, the rectangle.bottom is smaller than that before.
        int keypadHeight = screenHeight - rectangle.bottom;
        // 0.15 ratio is perhaps enough to determine keypad height.
        boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15;

        if (mKeyboardVisible != isKeyboardNowVisible) {
          if (isKeyboardNowVisible) {
            // onKeyboardShown
            hideBottomNavigation();
          } else {
            // onKeyboardHidden
            showBothNavigation();
          }
        }
        mKeyboardVisible = isKeyboardNowVisible;
      };

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
    if (onBackPressedListener != null) onBackPressedListener.doBack();
    else {
      DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
      if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        mDrawerLayout.closeDrawer(GravityCompat.START);
      else
        // navController.popBackStack();
        super.onBackPressed();
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    return NavigationUI.navigateUp(navController, appBarConfiguration)
        || super.onSupportNavigateUp();
  }
}
