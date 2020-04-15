package ch.epfl.favo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.ViewController;

import static androidx.navigation.Navigation.findNavController;
import static ch.epfl.favo.R.id.drawer_layout;

/**
 * This view will control all the fragments that are created. Contains a navigation drawer on the
 * left. Contains a bottom navigation for top-level activities.
 */
@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, ViewController {
  // Bottom tabs
  public RadioButton mapButton;
  public RadioButton favListButton;
  // UI
  private NavController navController;
  private NavigationView nav;
  private DrawerLayout drawerLayout;
  private ImageButton hambMenuButton;
  /*Activate if we want a toolbar */
  // private Toolbar toolbar;
  private ImageButton backButton;
  private boolean mKeyboardVisible = false;

  public Map<String, Favor> activeFavors;
  public Map<String, Favor> otherActiveFavorsAround;
  public Map<String, Favor> archivedFavors;
  public Favor focusedFavor = null;

  public OnBackPressedListener onBackPressedListener;

  public interface OnBackPressedListener {
    void doBack();
  }
  //  public ArrayList<Favor> getActiveFavorArrayList() {
  //    return activeFavorArrayList;
  //  }
  //
  //  public void addActiveFavor(Favor favor) {
  //    activeFavorArrayList.add(favor);
  //  }
  //
  //  public ArrayList<Favor> getarchivedFavorArrayList() {
  //    return archivedFavorArrayList;
  //  }
  //
  //  public void addPastFavor(Favor favor) {
  //    archivedFavorArrayList.add(favor);
  //  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize Variables
    nav = findViewById(R.id.nav_view);
    drawerLayout = findViewById(drawer_layout);
    hambMenuButton = findViewById(R.id.hamburger_menu_button);
    backButton = findViewById(R.id.back_button);
    mapButton = findViewById(R.id.nav_map_button);
    favListButton = findViewById(R.id.nav_favor_list_button);
    // Setup Controllers
    setUpHamburgerMenuButton();
    setUpBackButton();
    setupNavController();
    setupDrawerNavigation();
    setupBottomNavigation();

    // prevent swipe to open the navigation menu
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    // check connection
    if (DependencyFactory.isOfflineMode(this)) {
      showNoConnectionSnackbar();
    }

    activeFavors = new HashMap<>();
    archivedFavors = new HashMap<>();
    otherActiveFavorsAround = new HashMap<>();

    // Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    // setSupportActionBar(myToolbar);
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
            hideBottomTabs();
          } else {
            // onKeyboardHidden
            showBottomTabs();
          }
        }
        mKeyboardVisible = isKeyboardNowVisible;
      };

  private void setUpHamburgerMenuButton() {
    hambMenuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
  }

  private void setUpBackButton() {
    backButton.setOnClickListener(v -> onBackPressed());
  }

  private void setupNavController() {
    navController = findNavController(this, R.id.nav_host_fragment);
  }

  private void setupDrawerNavigation() {

    // Only pass top-level destinations.
    // appBarConfiguration = new AppBarConfiguration.Builder(R.id.map, R.id.fragment_favor).build();

    /*Activate if we want a toolbar */
    // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    nav.setNavigationItemSelectedListener(this);
  }

  /**
   * Will control drawer layout.
   *
   * @param item One of the buttons in the left drawer menu.
   * @return boolean of whether operation was successful.
   */
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();

    switch (itemId) {
      case R.id.nav_home:
        navController.popBackStack(R.id.nav_map, false);
        break;
      case R.id.nav_share:
        startShareIntent();
        break;
      default:
        navController.navigate(itemId);
    }

    drawerLayout.closeDrawer(GravityCompat.START);
    return false;
  }

  private void startShareIntent() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");

    shareIntent.putExtra(Intent.EXTRA_TITLE, "Favo app");
    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_site));

    startActivity(Intent.createChooser(shareIntent, null));
  }

  /** Will control the bottom navigation tabs */
  private void setupBottomNavigation() {
    mapButton.setOnClickListener(
        v -> {
          navController.popBackStack(R.id.nav_map, false);
        });
    favListButton.setOnClickListener(v -> navController.navigate(R.id.nav_favorlist));
  }

  /** Implementations of the ViewController interface below */
  @Override
  public void hideBottomTabs() {
    mapButton.setVisibility(View.INVISIBLE);
    favListButton.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showBottomTabs() {
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

  @Override
  public void checkMapViewButton() {
    favListButton.setChecked(false);
    mapButton.setChecked(true);
  }

  @Override
  public void checkFavListViewButton() {
    mapButton.setChecked(false);
    favListButton.setChecked(true);
  }

  @Override
  public void setupViewTopDestTab() {
    showBurgerIcon();
    showBottomTabs();
  }

  @Override
  public void setupViewBotDestTab() {
    hideBottomTabs();
    showBackIcon();
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
            otherActiveFavorsAround.put(favor.getId(), favor);
            Bundle favorBundle = new Bundle();
            favorBundle.putParcelable("FAVOR_ARGS", favor);
            navController.navigate(R.id.action_global_favorDetailView, favorBundle);
          });
    }
  }

  @Override
  public void onBackPressed() {
    if (onBackPressedListener != null) onBackPressedListener.doBack();
    else navController.popBackStack();
  }
}
