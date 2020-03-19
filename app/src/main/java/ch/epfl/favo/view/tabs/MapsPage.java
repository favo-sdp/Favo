package ch.epfl.favo.view.tabs;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.common.NoPermissionGrantedException;
import ch.epfl.favo.common.NoPositionFoundException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.map.GpsTracker;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.FakeFavorList;
import ch.epfl.favo.view.tabs.addFavor.FavorDetailView;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;
/**
 * View will contain a map and a favor request pop-up. It is implemented using the {@link Fragment}
 * subclass.
 */
public class MapsPage extends Fragment
    implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter {

  private GoogleMap mMap;
  private Location mLocation;
  private GpsTracker mGpsTracker;
  private ArrayList<Favor> currentActiveLocalFavorList = null;

  @Override
  public void onMapReady(GoogleMap googleMap) {
    setupView();
    mMap = googleMap;
    mMap.clear();
    drawSelfLocationMarker();
    drawFavorMarker(updateFavorlist());
    //throw new RuntimeException(
      //  "This exception will " + "certainly be thrown out as long as Map is ready");
  }

  public MapsPage() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    setupView();
    checkMapButton();

    return inflater.inflate(R.layout.tab1_map, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupView();
    checkMapButton();
    mGpsTracker = new GpsTracker(Objects.requireNonNull(getActivity()).getApplicationContext());
    SupportMapFragment mapFragment =
        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    if (mapFragment != null) {
      mapFragment.getMapAsync(this);
    }
  }


  public List<Favor> updateFavorlist() {
    // FavorUtil favorUtil = FavorUtil.getSingleInstance();
    // return favorUtil.retrieveAllFavorsInGivenRadius(mLocation, 2);
    if (mLocation != null) {
      FakeFavorList fakeFavorList =
          new FakeFavorList(mLocation.getLatitude(), mLocation.getLongitude(), mLocation.getTime());
      currentActiveLocalFavorList = fakeFavorList.retrieveFavorList();
      return currentActiveLocalFavorList;
    } else {
      FakeFavorList fakeFavorList = new FakeFavorList(20, 10, System.currentTimeMillis());
      currentActiveLocalFavorList = fakeFavorList.retrieveFavorList();
      return currentActiveLocalFavorList;
    }
  }

  public void drawSelfLocationMarker() {
    try {
      mLocation = mGpsTracker.getLocation();
      // Add a marker at my location and move the camera
      LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
      Marker me =
          mMap.addMarker(
              new MarkerOptions()
                  .position(myLocation)
                  .title("I am Here")
                  .draggable(true)
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, mMap.getMaxZoomLevel() - 5));
      mMap.setInfoWindowAdapter(this);
      mMap.setOnInfoWindowClickListener(this);

    } catch (NoPermissionGrantedException | NoPositionFoundException e) {
      CommonTools.showSnackbar(getView(), e.getMessage());
    }
  }
  /**
   * @param time the UTC time of this fix, in milliseconds since January 1, 1970.
   * @return human readable format of date and time
   */
  public String convertTime(long time) {
    Date date = new Date(time);
    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    return format.format(date);
  }

  private void setupView() {
    ((ViewController) getActivity()).showBurgerIcon();
    ((ViewController) getActivity()).showBottomTabs();
  }

  private void checkMapButton() {
    ((ViewController) getActivity()).checkMapViewButton();
  }

  /** utilities functions to help debug */
  //    public void displayDebugInfo() {
  //        Log.d("latitude", Double.toString(mLocation.getLatitude()));
  //        Log.d("longitude", Double.toString(mLocation.getLongitude()));
  //        Log.d("zoom", Float.toString(mMap.getMaxZoomLevel()));
  //        Log.d("gpstime", convertTime(mLocation.getTime()));
  //        Log.d("bearing", Float.toString(mLocation.getBearing()));
  //    }
  private void drawFavorMarker(List<Favor> favors) {
    if (favors == null) favors = new ArrayList<>();
    for (Favor favor : favors) {
      LatLng latLng =
          new LatLng(favor.getLocation().getLatitude(), favor.getLocation().getLongitude());
      Marker marker =
          mMap.addMarker(
              new MarkerOptions()
                  .position(latLng)
                  .title(favor.getTitle())
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
      StringBuffer summary = new StringBuffer();
      summary.append(favor.getDescription());
      marker.setSnippet(summary.toString());
    }
  }

  @Override
  public View getInfoWindow(Marker marker) {
    View mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
    String title = marker.getTitle();
    TextView titleUi = mWindow.findViewById(R.id.title);
    setSpannableString(title, titleUi);

    String snippet = marker.getSnippet();
    TextView snippetUi = mWindow.findViewById(R.id.snippet);
    setSpannableString(snippet, snippetUi);
    return mWindow;
  }
  /**
   * @param time the UTC time of this fix, in milliseconds since January 1, 1970.
   * @return human readable format of date and time
   *     <p>public String convertTime(long time) { Date date = new Date(time); Format format = new
   *     SimpleDateFormat("yyyy MM dd HH:mm:ss"); return format.format(date); }
   */
  /**
   * utilities functions to help debug
   *
   * <p>public void displayDebugInfo() { Log.d("latitude",
   * Double.toString(mLocation.getLatitude())); Log.d("longitude",
   * Double.toString(mLocation.getLongitude())); Log.d("zoom",
   * Float.toString(mMap.getMaxZoomLevel())); Log.d("gpstime", convertTime(mLocation.getTime()));
   * Log.d("bearing", Float.toString(mLocation.getBearing())); }
   */
}

  private void setSpannableString(String content, TextView view) {
    if (content != null) {
      SpannableString snippetText = new SpannableString(content);
      snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, content.length(), 0);
      view.setText(snippetText);
    }
  }

  @Override
  public View getInfoContents(Marker marker) {
    return null;
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
    // replaceFragment(new FavorDetailView(marker.getTitle(), marker.getSnippet()));
    if (marker.getTitle().equals("I am Here"))
      CommonTools.replaceFragment(
          R.id.nav_host_fragment, getParentFragmentManager(), new FavorRequestView());
    else
      CommonTools.replaceFragment(
          R.id.nav_host_fragment,
          getParentFragmentManager(),
          FavorDetailView.newInstance(
              queryFavor(marker.getPosition().latitude, marker.getPosition().longitude)));
  }

  public Favor queryFavor(double latitude, double longitude) {
    for (Favor favor : currentActiveLocalFavorList) {
      if (favor.getLocation().getLatitude() == latitude
          && favor.getLocation().getLongitude() == longitude)
        return favor;
    }
    return null;
  }
}