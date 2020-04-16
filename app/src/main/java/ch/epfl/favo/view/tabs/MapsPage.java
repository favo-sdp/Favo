package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.ViewController;
/**
 * View will contain a map and a favor request pop-up. It is implemented using the {@link Fragment}
 * subclass.
 */
@SuppressLint("NewApi")
public class MapsPage extends Fragment
    implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter {
  private GoogleMap mMap;
  private Location mLocation;
  private boolean first = true;
  //private FusedLocationProviderClient mFusedLocationProviderClient;
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private MainActivity activity;

  public MapsPage() {
    // Required empty public constructor
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onMapReady(GoogleMap googleMap) {
    setupView();
    mMap = googleMap;
    mMap.clear();
    mMap.setMyLocationEnabled(true);
    mMap.setInfoWindowAdapter(this);
    mMap.setOnInfoWindowClickListener(this);
    if (DependencyFactory.isOfflineMode(Objects.requireNonNull(getContext())))
      Objects.requireNonNull(getView()).findViewById(R.id.offline_map_button).setVisibility(View.VISIBLE);
    else
      Objects.requireNonNull(getView()).findViewById(R.id.offline_map_button).setVisibility(View.INVISIBLE);

    if(activity.focusedFavor != null){
      Favor favor = activity.focusedFavor;
      activity.focusedFavor = null;
      LatLng latLng = new LatLng(favor.getLocation().getLatitude(), favor.getLocation().getLongitude());
      Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(favor.getTitle())
              .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
      marker.setSnippet(favor.getDescription());
      marker.showInfoWindow();
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getMaxZoomLevel() - 5));
    }
    else{
      mLocation = DependencyFactory.getCurrentGpsTracker(getContext()).getLocation();
      updateNearbyList();
      drawSelfLocationMarker();
      drawFavorMarker(new ArrayList<>(activity.otherActiveFavorsAround.values()));
    }
  }

  private void onOfflineMapClick(View view) {
    new AlertDialog.Builder(Objects.requireNonNull(getContext()))
        .setTitle(R.string.offline_mode_dialog_title)
        .setMessage(R.string.offline_mode_instructions)
        .setPositiveButton(android.R.string.yes, null)
        .setNeutralButton(
            R.string.offline_mode_dialog_link,
            (dialogInterface, i) -> {
              Intent browserIntent =
                  new Intent(
                      Intent.ACTION_VIEW,
                      Uri.parse(
                          "https://support.google.com/maps/answer/6291838?co=GENIE.Platform%3DiOS&hl=en"));
              startActivity(browserIntent);
            })
        .show();
  }

  private void onToggleClick(View view){
    Navigation.findNavController(getView())
            .navigate(R.id.action_nav_map_to_nearby_favor_list, null);
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
    getLocationPermission();
    // mFusedLocationProviderClient =
    // LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
    // getLocation();
    View view = inflater.inflate(R.layout.fragment_map, container, false);

    FloatingActionButton button = view.findViewById(R.id.offline_map_button);
    button.setOnClickListener(this::onOfflineMapClick);

    RadioButton toggle = view.findViewById(R.id.list_switch);
    toggle.setOnClickListener(this::onToggleClick);
    activity = (MainActivity) Objects.requireNonNull(getActivity());
    setupView();
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    if (mapFragment != null)
      mapFragment.getMapAsync(this);
    return view;
  }

  private void updateNearbyList(){
    String setting = activity.getPreferences(Context.MODE_PRIVATE).getString("radius", "1km");
    double radius = 1;
    switch (setting){
      case "1 Km":
        radius = 1; break;
      case "5 Km":
        radius = 5; break;
      case "10 Km":
        radius = 10; break;
      case "25 Km":
        radius = 25; break;
    }
    CompletableFuture<List<Favor>> favors = FavorUtil.getSingleInstance()
            .retrieveAllFavorsInGivenRadius(mLocation, radius, activity);
    favors.whenComplete((e, res)->{
      if (first) {
        drawFavorMarker(new ArrayList<>(activity.otherActiveFavorsAround.values()));
        first = false;
    }});
  }


  private void getLocationPermission() {
    /**
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     **/
    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
    } else {
      ActivityCompat.requestPermissions(
          Objects.requireNonNull(getActivity()),
          new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  /*

    private void checkPlayServices() {
      GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
      int resultCode = gApi.isGooglePlayServicesAvailable(getActivity());
      if (resultCode != ConnectionResult.SUCCESS) {
        gApi.makeGooglePlayServicesAvailable(getActivity());
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

*/

  private void drawSelfLocationMarker() {
    // Add a marker at my location and move the camera
      LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
      Marker me =
          mMap.addMarker(
              new MarkerOptions()
                  .position(myLocation)
                  .title("FavorRequest")
                  .draggable(true)
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
      if (first) {
        first = false;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, mMap.getMaxZoomLevel() - 5));
      }
  }

  private void drawFavorMarker(List<Favor> favors) {
    if (favors.isEmpty()) {
      first = true;
      return;
    }
    for (Favor favor : favors) {
      LatLng latLng = new LatLng(favor.getLocation().getLatitude(), favor.getLocation().getLongitude());
      Marker marker = mMap.addMarker( new MarkerOptions().position(latLng).title(favor.getTitle())
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
      marker.setSnippet(favor.getDescription());
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
    if (marker.getTitle().equals(getString(R.string.self_location)))
      Navigation.findNavController(getView()).navigate(R.id.action_nav_map_to_favorRequestView);
    else {
      Favor favor = queryFavor(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle());
      Bundle favorBundle = new Bundle();
      favorBundle.putParcelable("FAVOR_ARGS", favor);
      Navigation.findNavController(getView())
          .navigate(R.id.action_nav_map_to_favorDetailView, favorBundle);
    }
  }

  private Favor queryFavor(double latitude, double longitude, String title) {
    for (Favor favor : activity.otherActiveFavorsAround.values()) {
      if (favor.getTitle().equals(title) && favor.getLocation().getLatitude() == latitude
          && favor.getLocation().getLongitude() == longitude) return favor;
    }
    return null;
  }
}
