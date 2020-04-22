package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
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
  // private FusedLocationProviderClient mFusedLocationProviderClient;
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private boolean mLocationPermissionGranted = false;
  private MainActivity activity;
  private View view;

  public MapsPage() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    // this is another, maybe better, way to get location, but cannot pass cirrus testing
    // mFusedLocationProviderClient =
    // LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
    // getLocation();
    getLocationPermission();
    view = inflater.inflate(R.layout.fragment_map, container, false);
    // setup offline map button
    FloatingActionButton button = view.findViewById(R.id.offline_map_button);
    button.setOnClickListener(this::onOfflineMapClick);
    if (DependencyFactory.isOfflineMode(requireContext())) button.setVisibility(View.VISIBLE);
    else button.setVisibility(View.INVISIBLE);

    // setup toggle between map and nearby list
    RadioButton toggle = view.findViewById(R.id.list_switch);
    toggle.setOnClickListener(this::onToggleClick);

    activity = (MainActivity) requireActivity();
    updateNearbyList();
    SupportMapFragment mapFragment =
        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    if (mapFragment != null && mLocationPermissionGranted) mapFragment.getMapAsync(this);
    return view;
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.clear();
    mMap.setMyLocationEnabled(true);
    mMap.setInfoWindowAdapter(this);
    mMap.setOnInfoWindowClickListener(this);
    if (activity.focusedFavor != null) {
      Favor favor = activity.focusedFavor;
      activity.focusedFavor = null;
      LatLng latLng =
          new LatLng(favor.getLocation().getLatitude(), favor.getLocation().getLongitude());
      Marker marker =
          mMap.addMarker(
              new MarkerOptions()
                  .position(latLng)
                  .title(favor.getTitle())
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
      marker.setSnippet(favor.getDescription());
      marker.showInfoWindow();
      marker.setTag(favor.getId());
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getMaxZoomLevel() - 5));
    } else {
      drawSelfLocationMarker();
      drawFavorMarker(new ArrayList<>(activity.otherActiveFavorsAround.values()));
    }
  }

  private void onOfflineMapClick(View view) {
    new AlertDialog.Builder(requireContext())
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

  private void onToggleClick(View view) {
    Navigation.findNavController(requireView()).navigate(R.id.action_nav_map_to_nearby_favor_list);
  }

  private void updateNearbyList() {
    String setting = activity.getPreferences(Context.MODE_PRIVATE).getString("radius", "1 Km");
    double radius = Double.parseDouble(setting.split(" ")[0]);

    try {
      mLocation = DependencyFactory.getCurrentGpsTracker(getContext()).getLocation();
      CompletableFuture<List<Favor>> favors =
          FavorUtil.getSingleInstance().retrieveAllFavorsInGivenRadius(mLocation, radius);
      favors.thenAccept(
          favors1 -> {
            ArrayList<Favor> favors2 = (ArrayList<Favor>) favors1;
            double latDif = Math.toDegrees(radius / 6371);
            for (Favor favor : favors2)
              if (!favor
                      .getRequesterId()
                      .equals(DependencyFactory.getCurrentFirebaseUser().getUid())
                  && favor.getStatusId() == FavorStatus.REQUESTED.toInt()
                  && favor.getLocation().getLatitude() > mLocation.getLatitude() - latDif
                  && favor.getLocation().getLongitude() < mLocation.getLatitude() + latDif)
                activity.otherActiveFavorsAround.put(favor.getId(), favor);
          });
      favors.exceptionally(
          e -> {
            CommonTools.showSnackbar(view, getString(R.string.nearby_favors_exception));
            return null;
          });
      favors.whenComplete(
          (e, res) -> {
            Log.d("pasS", "complete");
            if (first) {
              drawFavorMarker(new ArrayList<>(activity.otherActiveFavorsAround.values()));
              first = false;
            }
          });
    } catch (RuntimeException e) {
      CommonTools.showSnackbar(view, e.getMessage());
    }
  }

  /** Request location permission, so that we can get the location of the device. */
  private void getLocationPermission() {
    if (ContextCompat.checkSelfPermission(
            requireActivity().getApplicationContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(
          new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
      mLocationPermissionGranted = false;
    } else {
      mLocationPermissionGranted = true;
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode
        == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) { // If request is cancelled, the result arrays
      // are empty.
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mLocationPermissionGranted = true;
        Navigation.findNavController(view).navigate(R.id.action_global_nav_map);
      }
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
      LatLng latLng =
          new LatLng(favor.getLocation().getLatitude(), favor.getLocation().getLongitude());
      Marker marker =
          mMap.addMarker(
              new MarkerOptions()
                  .position(latLng)
                  .title(favor.getTitle())
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
      marker.setSnippet(favor.getDescription());
      marker.setTag(favor.getId());
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

  private void setSpannableString(String content, TextView textview) {
    if (content != null) {
      SpannableString snippetText = new SpannableString(content);
      snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, content.length(), 0);
      textview.setText(snippetText);
    }
  }

  @Override
  public View getInfoContents(Marker marker) {
    return null;
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
    if (marker.getTitle().equals(getString(R.string.self_location)))
      Navigation.findNavController(view).navigate(R.id.action_nav_map_to_favorRequestView);
    else {
      Favor favor = activity.otherActiveFavorsAround.get(marker.getTag());
      Bundle favorBundle = new Bundle();
      favorBundle.putParcelable("FAVOR_ARGS", favor);
      Navigation.findNavController(view)
          .navigate(R.id.action_nav_map_to_favorDetailView, favorBundle);
    }
  }
}
