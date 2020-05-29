package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.cache.CacheUtil;
import ch.epfl.favo.exception.NoPermissionGrantedException;
import ch.epfl.favo.exception.NoPositionFoundException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.gps.FavoLocation;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.addFavor.FavorEditingView;
import ch.epfl.favo.viewmodel.IFavorViewModel;

import static java.lang.Double.parseDouble;

/**
 * View will contain a map and a favor request pop-up. It is implemented using the {@link Fragment}
 * subclass.
 */
@SuppressLint("NewApi")
public class MapPage extends Fragment
    implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter {

  public static final String LOCATION_ARGUMENT_KEY = "LOCATION_ARGS";
  public static final String LATITUDE_ARGUMENT_KEY = "LATITUDE_ARGS";
  public static final String LONGITUDE_ARGUMENT_KEY = "LONGITUDE_ARGS";
  public static final int NEW_REQUEST = 1;
  public static final int EDIT_EXISTING_LOCATION = 2;
  public static final int SHARE_LOCATION = 3;
  public static final int OBSERVE_LOCATION = 4;
  private String latitudeFromChat;
  private String longitudeFromChat;
  private Button doneButton;

  private IFavorViewModel favorViewModel;
  private View view;
  private GoogleMap mMap;
  private Location mLocation;

  private Map<String, Marker> favorsAroundMe = new HashMap<>();
  private Favor focusedFavor;
  private double radiusThreshold;

  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private int defaultZoomLevel = 16;
  private ArrayList<Integer> mapStyles =
      new ArrayList<Integer>() {
        {
          add(R.raw.google_map_style_standard);
          add(R.raw.google_map_style_silver);
          add(R.raw.google_map_style_night);
        }
      };
  private static int intentType;
  private boolean mLocationPermissionGranted = false;
  private boolean firstOpenApp = true;
  private RadioButton nearbyFavorListToggle;
  private FloatingActionButton offlineBtn;
  private ArrayList<Marker> existAddedNewMarkers = new ArrayList<>();

  public MapPage() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getLocationPermission();
    view = inflater.inflate(R.layout.fragment_map, container, false);
    // setup offline map button
    offlineBtn = view.findViewById(R.id.offline_map_button);
    offlineBtn.setOnClickListener(this::onOfflineMapClick);

    if (DependencyFactory.isOfflineMode(requireContext())) offlineBtn.setVisibility(View.VISIBLE);
    else offlineBtn.setVisibility(View.INVISIBLE);
    favorViewModel =
        (IFavorViewModel)
            new ViewModelProvider(requireActivity())
                .get(DependencyFactory.getCurrentViewModelClass());
    // setup toggle between map and nearby list
    nearbyFavorListToggle = view.findViewById(R.id.list_switch);
    nearbyFavorListToggle.setOnClickListener(this::onToggleClick);

    SupportMapFragment mapFragment =
        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    if (mapFragment != null && mLocationPermissionGranted) mapFragment.getMapAsync(this);
    return view;
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    // set zoomLevel from user preference

    String radiusSetting =
        CacheUtil.getInstance()
            .getValueFromCacheStr(
                requireContext(), getString(R.string.radius_notifications_setting_key));
    double radiusThreshold = Integer.parseInt(getString(R.string.default_radius));
    if (!radiusSetting.equals("")) {
      radiusThreshold = Integer.parseInt(radiusSetting);
    }
    defaultZoomLevel = CommonTools.notificationRadiusToZoomLevel(radiusThreshold);

    // set map style from user preference
    String mapStyleSetting =
        CacheUtil.getInstance()
            .getValueFromCacheStr(requireContext(), getString(R.string.map_style_key));
    int map_mode_idx = Integer.parseInt(getString(R.string.map_style_default_value));
    if (!mapStyleSetting.equals("")) {
      map_mode_idx = Integer.parseInt(mapStyleSetting);
    }

    MapStyleOptions mapStyleOptions =
        MapStyleOptions.loadRawResourceStyle(requireContext(), mapStyles.get(map_mode_idx));
    googleMap.setMapStyle(mapStyleOptions);

    mMap = googleMap;
    mMap.clear();
    mMap.setMyLocationEnabled(true);
    mMap.setInfoWindowAdapter(this);
    mMap.setOnInfoWindowClickListener(this);
    mMap.setOnMapLongClickListener(new LongClick());
    mMap.setOnMarkerDragListener(new MarkerDrag());

    FloatingActionButton lookThroughBtn = view.findViewById(R.id.look_through_btn);
    lookThroughBtn.setOnClickListener(this::onLookThroughClick);

    try {
      mLocation = DependencyFactory.getCurrentGpsTracker(getContext()).getLocation();
    } catch (NoPermissionGrantedException e) {
      CommonTools.showSnackbar(requireView(), getString(R.string.no_position_permission_tip));
      return;
    } catch (NoPositionFoundException e) {
      CommonTools.showSnackbar(requireView(), getString(R.string.no_position_found_tip));
      return;
    } catch (Exception e) {
      CommonTools.showSnackbar(requireView(), getString(R.string.report_unknown_error));
      return;
    }
    try {
      if (!getViewModel().isShowObservedFavor() && intentType != 0) //
      {
        boolean isMarkerEditable = intentType != OBSERVE_LOCATION;
        drawMarkerAndFocusOnLocation(isMarkerEditable);
        doneButton.setOnClickListener(
            v -> {
              if (intentType == SHARE_LOCATION) sendLocationToChat(existAddedNewMarkers.get(0));
              else
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_nav_map_to_chatView);
            });

      } else { // intent is to edit or request
        setupNearbyFavorsListener();
        setupFocusedFavorListen();
        if (focusedFavor == null && firstOpenApp) {
          // only when the app is firstly opened, center on my location,
          // otherwise just return where I left before
          centerViewOnMyLocation();
          firstOpenApp = false;
        }
      }
    } catch (Exception e) {
      CommonTools.showSnackbar(requireView(), getString(R.string.error_database_sync));
    }
  }

  public Marker drawMarkerAndFocusOnLocation(boolean isEditable) {
    String markerTitle = isEditable ? getString(R.string.hint_drag_marker) : "";
    String markerDescription = isEditable ? getString(R.string.hint_click_window) : "";
    double selectedLongitude =
        (longitudeFromChat != null) ? parseDouble(longitudeFromChat) : mLocation.getLongitude();
    double selectedLatitude =
        (latitudeFromChat != null) ? parseDouble(latitudeFromChat) : mLocation.getLatitude();
    LatLng markerLocation = new LatLng(selectedLatitude, selectedLongitude);
    float markerColor = BitmapDescriptorFactory.HUE_GREEN;

    Marker marker =
        createMarker(markerLocation, markerColor, markerTitle, markerDescription, isEditable);
    existAddedNewMarkers.add(marker);
    focusViewOnLatLng(markerLocation, true);
    return marker;
  }

  private void setLimitedView() {
    view.findViewById(R.id.toggle).setVisibility(View.GONE);
    ((MainActivity) requireActivity()).hideBottomNavigation();

    requireActivity().findViewById(R.id.hamburger_menu_button).setVisibility(View.GONE);
    doneButton = requireView().findViewById(R.id.button_location_from_request_view);
    doneButton.setVisibility(View.VISIBLE);
    setupToolbar(intentType);
  }

  private void setupToolbar(int intentType) {
    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar_main_activity);
    toolbar.setNavigationIcon(null);
    toolbar.setBackgroundColor(getResources().getColor(R.color.material_green_500));
    toolbar.setTitleTextColor(Color.WHITE);

    switch (intentType) {
      case NEW_REQUEST:
        {
          toolbar.setTitle(R.string.map_request_favor);
          break;
        }
      case EDIT_EXISTING_LOCATION:
        {
          toolbar.setTitle(R.string.map_edit_favor_loc);
          break;
        }
      case SHARE_LOCATION:
        {
          toolbar.setTitle(R.string.map_share_loc);
          break;
        }
      case OBSERVE_LOCATION:
        {
          toolbar.setTitle(R.string.map_observe_loc);
          break;
        }
    }
  }

  public void setFocusedFavor(Favor favor) {
    focusedFavor = favor;
  }

  @Override
  public void onStart() {
    super.onStart();
    if (getArguments() != null) {
      intentType = getArguments().getInt(LOCATION_ARGUMENT_KEY);
      latitudeFromChat = getArguments().getString(LATITUDE_ARGUMENT_KEY);
      longitudeFromChat = getArguments().getString(LONGITUDE_ARGUMENT_KEY);
    }

    if (intentType != 0) { // if intent is to edit, request, share, or observe
      setLimitedView();
    }
  }

  private class MarkerDrag implements GoogleMap.OnMarkerDragListener {

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {
      mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), defaultZoomLevel));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
      if (intentType == SHARE_LOCATION) sendLocationToChat(existAddedNewMarkers.get(0));
    }
  }

  private class LongClick implements GoogleMap.OnMapLongClickListener {
    @Override
    public void onMapLongClick(LatLng latLng) {
      if (intentType != 0) return;
      // at most one new marker is allowed
      if (existAddedNewMarkers.size() != 0) {
        for (Marker m : existAddedNewMarkers) m.remove();
        existAddedNewMarkers.clear();
      }
      FavoLocation loc = new FavoLocation(mLocation);
      loc.setLatitude(latLng.latitude);
      loc.setLongitude(latLng.longitude);
      focusedFavor =
          new Favor(
              "",
              " ",
              DependencyFactory.getCurrentFirebaseUser().getUid(),
              loc,
              FavorStatus.EDIT,
              0);

      Marker mk = drawFavorMarker(focusedFavor, true, true);
      mk.showInfoWindow();
      existAddedNewMarkers.add(mk);
      mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }
  }

  private void setupFocusedFavorListen() {

    getViewModel()
        .getObservedFavor()
        .observe(
            getViewLifecycleOwner(),
            favor -> {
              try {
                if (favor != null && favorViewModel.isShowObservedFavor()) {
                  favorViewModel.setShowObservedFavor(false);
                  setFocusedFavor(favor);
                  boolean isRequested = // check if favor is requested
                      favor
                          .getRequesterId()
                          .equals(DependencyFactory.getCurrentFirebaseUser().getUid());
                  boolean isEdited = focusedFavor.getStatusId() == FavorStatus.EDIT.toInt();
                  Marker marker = drawFavorMarker(focusedFavor, isRequested, isEdited);
                  if (isEdited) {
                    existAddedNewMarkers.add(marker);
                    doneButton.setOnClickListener(
                        v -> requestFavorOnMarkerLocation(existAddedNewMarkers.get(0)));
                  }
                  marker.showInfoWindow();
                  focusViewOnLocation(focusedFavor.getLocation(), true);
                }
              } catch (Exception e) {
                CommonTools.showSnackbar(requireView(), getString(R.string.error_database_sync));
              }
            });
  }

  private void sendLocationToChat(Marker marker) {

    Bundle chatBundle = new Bundle();
    chatBundle.putParcelable(LOCATION_ARGUMENT_KEY, marker.getPosition());
    Navigation.findNavController(requireView())
        .navigate(R.id.action_nav_map_to_chatView, chatBundle);
  }

  private void requestFavorOnMarkerLocation(Marker marker) {
    focusedFavor.getLocation().setLatitude(marker.getPosition().latitude);
    focusedFavor.getLocation().setLongitude(marker.getPosition().longitude);
    int change = (intentType == NEW_REQUEST) ? 1 : 0;
    // post to DB
    CompletableFuture<Void> postFavorFuture = getViewModel().requestFavor(focusedFavor, change);
    postFavorFuture.whenComplete(
        (aVoid, throwable) -> {
          if (throwable != null)
            CommonTools.showSnackbar(
                requireView(),
                getString(
                    CommonTools.getSnackbarMessageForFailedRequest(
                        (CompletionException) throwable)));
          else {
            CommonTools.showSnackbar(
                requireView(),
                getString(CommonTools.getSnackbarMessageForRequestedFavor(requireContext())));
            // jump to favorPublished view
            Bundle favorBundle = new Bundle();
            favorBundle.putString(CommonTools.FAVOR_ARGS, focusedFavor.getId());
            Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_map_to_favorPublishedView_via_RequestView, favorBundle);
          }
        });
  }

  public IFavorViewModel getViewModel() {
    return favorViewModel;
  }

  private Marker drawFavorMarker(Favor favor, boolean isRequested, boolean isEdited) {
    LatLng latLng =
        new LatLng(favor.getLocation().getLatitude(), favor.getLocation().getLongitude());
    float markerColor =
        isRequested ? BitmapDescriptorFactory.HUE_ORANGE : BitmapDescriptorFactory.HUE_VIOLET;
    String markerTitle =
        (isEdited) && favor.getTitle().equals("")
            ? getString(R.string.hint_drag_marker)
            : favor.getTitle();
    String markerDescription =
        isEdited && favor.getDescription().equals("")
            ? getString(R.string.hint_click_window)
            : favor.getDescription();
    Marker marker = createMarker(latLng, markerColor, markerTitle, markerDescription, isEdited);
    marker.setTag(
        new ArrayList<Object>() {
          {
            add(favor.getId());
            add(isRequested);
            add(false);
          }
        });
    if (!isRequested) {
      favorsAroundMe.put(favor.getId(), marker);
    }
    return marker;
  }

  private Marker createMarker(
      LatLng latLng,
      float markerColor,
      String markerTitle,
      String markerDescription,
      boolean isDraggable) {
    Marker marker =
        mMap.addMarker(
            new MarkerOptions()
                .position(latLng)
                .title(markerTitle)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                .snippet(markerDescription));
    marker.setDraggable(isDraggable);
    if (isDraggable) marker.showInfoWindow();
    return marker;
  }

  private void focusViewOnLocation(Location location, boolean animate) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    focusViewOnLatLng(latLng, animate);
  }

  private void focusViewOnLatLng(LatLng location, boolean animate) {
    if (animate) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, defaultZoomLevel));
    else mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, defaultZoomLevel));
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
                      Intent.ACTION_VIEW, Uri.parse(getString(R.string.download_offline_map)));
              startActivity(browserIntent);
            })
        .show();
  }

  private void onToggleClick(View view) {
    Navigation.findNavController(requireView()).navigate(R.id.action_nav_map_to_nearby_favor_list);
  }

  private void onLookThroughClick(View view) {
    for (Marker marker : favorsAroundMe.values()) {
      ArrayList tagArray = (ArrayList) marker.getTag();
      boolean visited = (boolean) tagArray.get(2);
      if (!visited) {
        // mark that this favor has been visited
        tagArray.set(2, true);
        marker.showInfoWindow();
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(marker.getPosition(), defaultZoomLevel + 2));
        return;
      }
    }
    // if all visited
    for (Marker marker : favorsAroundMe.values()) {
      ArrayList tagArray = (ArrayList) marker.getTag();
      tagArray.set(2, false);
    }
    Toast.makeText(requireContext(), getString(R.string.finish_visit_marker), Toast.LENGTH_SHORT)
        .show();
  }

  private void setupNearbyFavorsListener() {
    favorViewModel
        .getFavorsAroundMe(mLocation, radiusThreshold)
        .observe(
            getViewLifecycleOwner(),
            stringFavorMap -> {
              try {
                drawFavorMarkers(new ArrayList<>(stringFavorMap.values()));
              } catch (Exception e) {
                CommonTools.showSnackbar(
                    requireView(), getString(R.string.nearby_favors_exception));
              }
            });
  }

  private void drawFavorMarkers(List<Favor> favors) {
    for (Favor favor : favors) {
      drawFavorMarker(favor, false, false);
    }
  }

  private void centerViewOnMyLocation() {
    // Add a marker at my location and move the camera
    focusViewOnLocation(mLocation, false);
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
        == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) { // If fragment_favor_published_view is
      // cancelled, the result arrays
      // are empty.
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mLocationPermissionGranted = true;
        // if get permission, then refresh the map view
        Navigation.findNavController(view).navigate(R.id.action_global_nav_map);
      }
    }
  }

  @Override
  public View getInfoWindow(Marker marker) {
    View mWindow = getLayoutInflater().inflate(R.layout.map_info_window, null);
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
    if (intentType != 0) return;
    List<Object> markerInfo = (List<Object>) marker.getTag();
    String favorId = markerInfo.get(0).toString();
    boolean isRequested = (boolean) markerInfo.get(1);
    Bundle favorBundle = new Bundle();
    if (isRequested && focusedFavor.getStatusId() == FavorStatus.EDIT.toInt()) {
      navigateToEditPage(marker, favorBundle);
    } else if (focusedFavor == null || focusedFavor.getStatusId() != FavorStatus.EDIT.toInt()) {
      favorBundle.putString(CommonTools.FAVOR_ARGS, favorId);
      Navigation.findNavController(view)
          .navigate(R.id.action_nav_map_to_favorPublishedView, favorBundle);
    }
  }

  private void navigateToEditPage(Marker marker, Bundle favorBundle) {
    focusedFavor.getLocation().setLatitude(marker.getPosition().latitude);
    focusedFavor.getLocation().setLongitude(marker.getPosition().longitude);
    favorBundle.putParcelable(CommonTools.FAVOR_VALUE_ARGS, focusedFavor);
    favorBundle.putString(FavorEditingView.FAVOR_SOURCE_KEY, FavorEditingView.FAVOR_SOURCE_MAP);
    Navigation.findNavController(view)
        .navigate(R.id.action_nav_map_to_favorEditingView, favorBundle);
  }
}
