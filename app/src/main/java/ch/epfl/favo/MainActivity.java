package ch.epfl.favo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Objects;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.view.ViewController;
import ch.epfl.favo.view.tabs.addFavor.FavorDetailView;

import static androidx.navigation.Navigation.findNavController;
import static ch.epfl.favo.R.id.drawer_layout;
// import static ch.epfl.favo.R.id.toolbar;

/**
 * This view will control all the fragments that are created. Contains a navigation drawer on the
 * left. Contains a bottom navigation for top-level activities.
 */
public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, ViewController {
  private static final String TAG = "MainActivity";
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

  private ArrayList<Favor> activeFavorArrayList;
  private ArrayList<Favor> archivedFavorArrayList;

  public ArrayList<Favor> getActiveFavorArrayList() {
    return activeFavorArrayList;
  }

  public void addActiveFavor(Favor favor) {
    activeFavorArrayList.add(favor);
  }

  public ArrayList<Favor> getarchivedFavorArrayList() {
    return archivedFavorArrayList;
  }

  public void addPastFavor(Favor favor) {
    archivedFavorArrayList.add(favor);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // retrieve current registration token for notifications
    retrieveCurrentRegistrationToken();

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

    /*Activate if we want a toolbar */
    // toolbar = findViewById(R.id.toolbar);
    // setSupportActionBar(toolbar);

    activeFavorArrayList = new ArrayList<>();
    archivedFavorArrayList = new ArrayList<>();
  }

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
        navController.navigate(R.id.nav_map);
        break;
      case R.id.nav_share:
        startShareIntent();
        break;
      default:
        navController.navigate(itemId);
    }

    drawerLayout.closeDrawer(GravityCompat.START);
    return true;
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
    mapButton.setOnClickListener(v -> navController.navigate(R.id.nav_map));
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

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Bundle extras = intent.getExtras();
    if (extras!=null){
      String favor_id = extras.getString("FavorId");
      Favor favor = FavorUtil.getSingleInstance().retrieveFavor(favor_id);
      Fragment frag  = FavorDetailView.newInstance(favor);
      FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
      trans.replace(R.id.nav_host_fragment,frag);
      trans.commit();
    }


  }

  @Override
  public void onBackPressed() {
    getSupportFragmentManager().popBackStackImmediate();
  }

  // retrieve current registration token for the notification system
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
              Log.d(TAG, getString(R.string.msg_token_fmt, token));
              // TODO send registration token to db
            });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }
}
