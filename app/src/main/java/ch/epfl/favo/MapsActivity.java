package ch.epfl.favo;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GpsTracker gpsTracker;
    private Location mLocation;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the position of phone with some permission checking
        gpsTracker = new GpsTracker(getApplicationContext());
        try{
            mLocation = gpsTracker.getLocation();
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        }
        catch (Exception e){
            View view = findViewById(R.id.map);
            Snackbar mySnackbar = Snackbar.make(view, "No location found", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near my location.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker at my location and move the camera
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("I am Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, mMap.getMaxZoomLevel() - 5));
        displayDebugInfo();
    }

    /**
     *
     * @param time the UTC time of this fix, in milliseconds since January 1, 1970.
     * @return human readable format of date and time
     */
    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * utilities functions to help debug
     */
    public void displayDebugInfo(){
        Log.d("latitude", Double.toString(mLocation.getLatitude()));
        Log.d("longitude", Double.toString(mLocation.getLongitude()));
        Log.d("zoom", Float.toString(mMap.getMaxZoomLevel()));
        Log.d("gpstime", convertTime(mLocation.getTime()));
        Log.d("bearing", Float.toString(mLocation.getBearing()));
    }
}

