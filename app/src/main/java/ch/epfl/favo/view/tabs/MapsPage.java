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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.Map;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.FavorDataController;

/**
 * View will contain a map and a favor request pop-up. It is implemented using the {@link Fragment}
 * subclass.
 */
@SuppressLint("NewApi")
public class MapsPage extends Fragment
    implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter {
  private FavorDataController favorViewModel;
  private Map<String, Favor> favorsAroundMe;
  private double radiusThreshold;
  private GoogleMap mMap;
  private Location mLocation;
  private Favor focusedFavor;
  private boolean first = true;
  // private FusedLocationProviderClient mFusedLocationProviderClient;
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private boolean mLocationPermissionGranted = false;
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
    favorViewModel =
        (FavorDataController)
            new ViewModelProvider(requireActivity()).get(DependencyFactory.getCurrentViewModelClass());
    // setup toggle between map and nearby list
    RadioButton toggle = view.findViewById(R.id.list_switch);
    toggle.setOnClickListener(this::onToggleClick);

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
    String setting =
        requireActivity().getPreferences(Context.MODE_PRIVATE).getString("radius", "10 Km");
    radiusThreshold = Double.parseDouble(setting.split(" ")[0]);
    try {
      mLocation = DependencyFactory.getCurrentGpsTracker(getContext()).getLocation();
    } catch (Exception e) {
      CommonTools.showSnackbar(requireView(), e.getMessage());
      return;
    }
    try {
      setupNearbyFavorsListener();
      setupFocusedFavorListen();
    } catch (Exception e) {
      CommonTools.showSnackbar(requireView(), getString(R.string.error_database_sync));
    }
    if (focusedFavor == null) centerViewOnMyLocation();
  }

  private void setupFocusedFavorListen() {

    getViewModel()
        .getObservedFavor()
        .observe(
            getViewLifecycleOwner(),
            favor -> {
              try {
                if (favor != null) {
                  focusedFavor = favor;
                  boolean isRequested = // check if favor is requested
                      favor
                          .getRequesterId()
                          .equals(DependencyFactory.getCurrentFirebaseUser().getUid());

                  Marker marker = drawFavorMarker(focusedFavor, isRequested);
                  marker.showInfoWindow();
                  focusViewOnLocation(focusedFavor.getLocation());
                }
              } catch (Exception e) {
                CommonTools.showSnackbar(requireView(), getString(R.string.error_database_sync));
              }
            });
  }

  public FavorDataController getViewModel() {
    return favorViewModel;
  }

  private Marker drawFavorMarker(Favor favor, boolean isRequested) {
    LatLng latLng =
        new LatLng(favor.getLocation().getLatitude(), favor.getLocation().getLongitude());
    float markerColor =
        isRequested ? BitmapDescriptorFactory.HUE_AZURE : BitmapDescriptorFactory.HUE_RED;
    Marker marker =
        mMap.addMarker(
            new MarkerOptions()
                .position(latLng)
                .title(favor.getTitle())
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
    marker.setSnippet(favor.getDescription());
    marker.setTag(
        new ArrayList<Object>() {
          {
            add(favor.getId());
            add(isRequested);
          }
        });
    return marker;
  }

  private void focusViewOnLocation(Location location) {
    LatLng latLng =
        new LatLng(
            location.getLatitude(),
            location.getLongitude()); // TODO: Constrain map to preference radius
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getMaxZoomLevel() - 5));
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

  private void setupNearbyFavorsListener() {

    getViewModel()
        .getFavorsAroundMe(mLocation, radiusThreshold)
        .observe(
            getViewLifecycleOwner(),
            stringFavorMap -> {
              try {
                favorsAroundMe = stringFavorMap;
                drawFavorMarkers(new ArrayList<>(favorsAroundMe.values()));
                first = false;
              } catch (Exception e) {
                CommonTools.showSnackbar(
                    requireView(), getString(R.string.nearby_favors_exception));
              }
            });
  }

  /** Request location permission, so that we can get the location of the device. */
  private void getLocationPermission() {
    /** Request location permission, so that we can get the location of the device. */
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

  private void centerViewOnMyLocation() {
    // Add a marker at my location and move the camera
    focusViewOnLocation(mLocation);
  }

  private void drawFavorMarkers(List<Favor> favors) {
    if (favors.isEmpty()) {
      first = true;
      return;
    }
    for (Favor favor : favors) {
      drawFavorMarker(favor, false);
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
    List<Object> markerInfo = (List<Object>) marker.getTag();
    String favorId = markerInfo.get(0).toString();
    boolean isRequested = (boolean) markerInfo.get(1);
    Bundle favorBundle = new Bundle();
    favorBundle.putString("FAVOR_ARGS", favorId);

    if (isRequested) {
      Navigation.findNavController(view).navigate(R.id.action_global_favorRequestView, favorBundle);
    } else {
      Navigation.findNavController(view)
          .navigate(R.id.action_nav_map_to_favorDetailView, favorBundle);
    }
  }
}
