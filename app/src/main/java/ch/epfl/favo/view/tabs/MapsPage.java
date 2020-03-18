package ch.epfl.favo.view.tabs;


import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.favo.R;
import ch.epfl.favo.map.GpsTracker;
import ch.epfl.favo.view.ViewController;

/**
 * View will contain a map and a favor request pop-up. It is implemented using the {@link Fragment}
 * subclass.
 */
public class MapsPage extends Fragment {


  private GoogleMap mMap;
  private Location mLocation;
  private GpsTracker mGpsTracker;
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
          double latitude, longitude;
          mLocation = mGpsTracker.getLocation();
          latitude = mLocation.getLatitude();
          longitude = mLocation.getLongitude();
          // Add a marker at my location and move the camera
          LatLng myLocation = new LatLng(latitude, longitude);
          Marker myPos =
              mMap.addMarker(new MarkerOptions().position(myLocation).title("I am Here"));
          myPos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
          myPos.setFlat(true);

          LatLng otherUserLocation = new LatLng(latitude + 0.001, longitude + 0.001);
          Marker otherPos =
              mMap.addMarker(
                  new MarkerOptions().position(otherUserLocation).title("Another user/favor"));
          otherPos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
          otherPos.setFlat(false);
          otherPos.setSnippet("Description of favor");

          otherUserLocation = new LatLng(latitude + 0.006, longitude - 0.004);
          otherPos =
              mMap.addMarker(
                  new MarkerOptions().position(otherUserLocation).title("Another user/favor"));
          otherPos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
          otherPos.setFlat(false);
          otherPos.setSnippet("Description of favor");

          mMap.moveCamera(
              CameraUpdateFactory.newLatLngZoom(myLocation, mMap.getMaxZoomLevel() - 5));
          /*  displayDebugInfo();*/
        }
      };

  public MapsPage() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
      setupView();



    return inflater.inflate(R.layout.tab1_map, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupView();
    mGpsTracker = new GpsTracker(getActivity().getApplicationContext());
    SupportMapFragment mapFragment =
        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    if (mapFragment != null) {
      mapFragment.getMapAsync(callback);
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

    private void setupView(){
        ((ViewController) getActivity()).setupViewTopDestTab();
        ((ViewController) getActivity()).checkMapViewButton();
    }

    /**
     * utilities functions to help debug
     */
//    public void displayDebugInfo() {
//        Log.d("latitude", Double.toString(mLocation.getLatitude()));
//        Log.d("longitude", Double.toString(mLocation.getLongitude()));
//        Log.d("zoom", Float.toString(mMap.getMaxZoomLevel()));
//        Log.d("gpstime", convertTime(mLocation.getTime()));
//        Log.d("bearing", Float.toString(mLocation.getBearing()));
//    }


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
