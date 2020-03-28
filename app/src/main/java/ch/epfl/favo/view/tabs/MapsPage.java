package ch.epfl.favo.view.tabs;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
import ch.epfl.favo.view.ViewController;
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
  private ArrayList<Favor> currentActiveLocalFavorList = null;
  private boolean first=true;
  private GpsTracker mGpsTracker;
  private boolean mLocationPermissionGranted = false;
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

  public MapsPage() {
    // Required empty public constructor
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    setupView();
    mMap = googleMap;
    mMap.clear();
    drawSelfLocationMarker();
    drawFavorMarker(updateFavorlist());
  }

  private void setupView() {
    ((ViewController) getActivity()).setupViewTopDestTab();
    ((ViewController) getActivity()).checkMapViewButton();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    setupView();
   // mFusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
   // getLocation();
    return inflater.inflate(R.layout.tab1_map, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupView();
    SupportMapFragment mapFragment =
        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    mGpsTracker = new GpsTracker(getContext());
    if (mapFragment != null) {
      mapFragment.getMapAsync(this);
    }
  }

  private void checkPlayServices() {
    GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
    int resultCode = gApi.isGooglePlayServicesAvailable(getActivity());
    if (resultCode != ConnectionResult.SUCCESS) {
      gApi.makeGooglePlayServicesAvailable(getActivity());
    }
  }

  private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
      mLocationPermissionGranted = true;
    } else {
      ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
              new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
              PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  public void getLocation() throws NoPermissionGrantedException, NoPositionFoundException {
    getLocationPermission();
    if (mLocationPermissionGranted) {
      LocationRequest request = new LocationRequest();
      request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
      request.setInterval(15 * 60 * 1000);
      request.setMaxWaitTime(30 * 60 * 1000);
      Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
      locationResult.addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<Location>() {
        @Override
        public void onComplete(@NonNull Task<Location> task) {
          if (task.isSuccessful()) {
            // Set the map's camera position to the current location of the device.
            mLocation = task.getResult();
            GpsTracker.setLastKnownLocation(mLocation);
          }
        }
      });
    }
  }

  /**
   * Handles the result of the request for location permissions.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    mLocationPermissionGranted = false;
    if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
      if (grantResults.length > 0
              && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mLocationPermissionGranted = true;
      }
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

  private void drawSelfLocationMarker() {
      // Add a marker at my location and move the camera
    try{
      mLocation = mGpsTracker.getLocation();
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
    }
    catch (Exception e){
      CommonTools.showSnackbar(getView(), e.getMessage());
    }
  }

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
    if (marker.getTitle().equals(getString(R.string.self_location)))
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
          && favor.getLocation().getLongitude() == longitude) return favor;
    }
    return null;
  }
}
