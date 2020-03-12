package ch.epfl.favo.view.tabs;


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
public class MapsPage extends TopDestinationTab implements
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter {


  private GoogleMap mMap;
  private Location mLocation;
  private GpsTracker mGpsTracker;
    private ArrayList<Favor> currentActiveLocalFavorList;
  private OnMapReadyCallback callback =
      new OnMapReadyCallback() {

        /**
         * Manipulates the map once available. This callback is triggered when the map is ready to
         * be used. This is where we can add markers or lines, add listeners or move the camera.
         *
         * <p>In this case, we just add a marker near Sydney, Australia. If Google Play services is
         * not installed on the device, the user will be prompted to install it inside the
         * SupportMapFragment. This method will only be triggered once the user has installed Google
         * Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
          mMap = googleMap;
          mMap.clear();
            drawSelfLocationMarker();
            drawFavorMarker(updateFavorlist());
        }
      };

  public MapsPage() {
    // Required empty public constructor
  }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab1_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGpsTracker = new GpsTracker(Objects.requireNonNull(getActivity()).getApplicationContext());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    private List<Favor> updateFavorlist() {
    //FavorUtil favorUtil = FavorUtil.getSingleInstance();
    //return favorUtil.retrieveAllFavorsInGivenRadius(mLocation, 2);
        FakeFavorList fakeFavorList = new FakeFavorList(mLocation);
        currentActiveLocalFavorList = fakeFavorList.retrieveFavorList();
        return currentActiveLocalFavorList;
  }

    private void drawSelfLocationMarker() {
    try{
      mLocation = mGpsTracker.getLocation();
      // Add a marker at my location and move the camera
      LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
      Marker me = mMap.addMarker(new MarkerOptions()
          .position(myLocation)
          .title("I am Here")
              .draggable(true)
              .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
      );

      mMap.moveCamera(
              CameraUpdateFactory.newLatLngZoom(myLocation, mMap.getMaxZoomLevel() - 5));
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
    }
    catch (NoPermissionGrantedException | NoPositionFoundException e){
      CommonTools.showSnackbar(getView() ,e.getMessage());
      }
    }


    private void drawFavorMarker(List<Favor> favors) {
        if (favors == null)
            favors = new ArrayList<>();
        for (Favor favor : favors) {
            LatLng latLng = new LatLng(favor.getLocation().getLatitude(), favor.getLocation().getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
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
      if (title != null) {
        // Spannable string allows us to edit the formatting of the text.
        SpannableString titleText = new SpannableString(title);
        titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
        titleUi.setText(titleText);
      }

      String snippet = marker.getSnippet();
      TextView snippetUi = mWindow.findViewById(R.id.snippet);
      if (snippet != null) {
        SpannableString snippetText = new SpannableString(snippet);
        snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, snippet.length(), 0);
        snippetUi.setText(snippetText);
      }
      return mWindow;
    }


    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        //replaceFragment(new FavorDetailView(marker.getTitle(), marker.getSnippet()));
        if (marker.getTitle().equals("I am Here"))
            CommonTools.replaceFragment(getParentFragmentManager(), new FavorRequestView());
        else
            CommonTools.replaceFragment(getParentFragmentManager(), new FavorDetailView(
                    queryFavor(marker.getPosition().latitude, marker.getPosition().longitude)));

    }

    private Favor queryFavor(double latitude, double longitude) {
        for (Favor favor : currentActiveLocalFavorList) {
            if (favor.getLocation().getLatitude() == latitude
                    && favor.getLocation().getLongitude() == longitude)
                return favor;
        }
        return null;
    }


}
