package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.common.DatabaseWrapper;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.map.GpsTracker;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.FavorFragmentFactory;
import ch.epfl.favo.util.TaskToFutureAdapter;
import ch.epfl.favo.view.ViewController;
import ch.epfl.favo.view.tabs.addFavor.FavorDetailView;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;
import ch.epfl.favo.view.tabs.favorList.FavorAdapter;

import static ch.epfl.favo.util.CommonTools.hideKeyboardFrom;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */
public class NearbyFavorList extends Fragment implements View.OnClickListener {

    private Map<String, Favor> nearbyFavors = new HashMap<>();
    private TextView tipTextView;
    private ListView listView;
    private SearchView searchView;
    private int screenWidth;
    private boolean first = true;
    private Map<String, Favor> favorsFound = new HashMap<>();

    public NearbyFavorList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // Extract two arrayLists from the main activity
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_nearby_favor_list, container, false);
        setupView(rootView);
        tipTextView = rootView.findViewById(R.id.nearby_tip);
        tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        listView = rootView.findViewById(R.id.nearby_favor_list);
        searchView = rootView.findViewById(R.id.nearby_searchView);
        FloatingActionButton toggle = rootView.findViewById(R.id.list_toggle);
        toggle.setOnClickListener(this::onToggleClick);
        first = true;
        Log.d("ListView", "create view");
        setupListView();
        setupNearbyList();
        setupSearchView();
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupNearbyList(){
        MainActivity activity = (MainActivity) Objects.requireNonNull(getActivity());
        String setting = activity.getPreferences(Context.MODE_PRIVATE).getString("radius", "1km");
        double radius = 1;
        Log.d("Radius", setting);
        switch (setting){
            case "1 Km":
                radius = 1;
                break;
            case "5 Km":
                radius = 5;
                break;
            case "10 Km":
                radius = 10;
                break;
            case "25 Km":
                radius = 25;
                break;
        }
        GpsTracker mGpsTracker = new GpsTracker(getContext());
        Location loc = mGpsTracker.getLocation();
        CompletableFuture<List<Favor>> favors = retrieveLongitudeBoundFavors(loc, radius);
        double finalRadius = radius;
        favors.thenAccept(favors1 -> {
            Log.d("Radius", String.valueOf(favors1.size()));
            double latDif = Math.toDegrees(finalRadius / 6371);
            double latitude_lower = loc.getLatitude() - latDif;
            double latitude_upper = loc.getLatitude() + latDif;
            for (Favor favor:favors1) {
                if(favor.getLocation().getLatitude() > latitude_lower
                        && favor.getLocation().getLongitude() < latitude_upper)
                nearbyFavors.put(favor.getId(), favor);
                activity.otherActiveFavorsAround = nearbyFavors;
            }
            if(!favorsFound.isEmpty())
                displayFavorList(favorsFound, R.string.query_failed);
            else
                displayFavorList(nearbyFavors, R.string.favor_no_active_favor);
        });
    }

  /**
   * I currently implement a temporary, simpler version to retrieve favors in a square area on
   * sphere surface. It should be replaced by
   * FavorUtil.getSingleInstance().retrieveAllFavorsInGivenRadius(mGpsTracker.getLocation(), radius);
   * in future.
   * @param loc
   * @param radius
   */
  @RequiresApi(api = Build.VERSION_CODES.N)
  private CompletableFuture<List<Favor>> retrieveLongitudeBoundFavors(Location loc, double radius) {

        double longDif = Math.toDegrees(radius / (6371 * Math.cos(Math.toRadians(loc.getLatitude()))));
        double longitude_lower = loc.getLongitude() - longDif;
        double longitude_upper = loc.getLongitude() + longDif;
        Task<QuerySnapshot> getAllTask = DatabaseWrapper.getCollectionReference("favors")
                //.whereGreaterThan("location.latitude", latitude_lower)
                //.whereLessThan("location.latitude", latitude_upper)
                .whereGreaterThan("location.longitude", 6.6)
                .whereLessThan("location.longitude", 7).get();
        Log.d("Radius", longitude_lower + " " + longitude_upper + " " + loc.getLongitude());
        CompletableFuture<QuerySnapshot> getAllFuture = new TaskToFutureAdapter<>(getAllTask);

        return getAllFuture.thenApply(
                querySnapshot -> querySnapshot.toObjects(Favor.class));
    }

    private void setupSearchView(){
        searchView.setIconifiedByDefault(true);
        searchView.setTag("SearchView");
        // if returned from FavorDetail view, continue to show the search mode
        if(!favorsFound.isEmpty()){
            searchView.setIconified(false);
            searchView.clearFocus();
            setupSearchMode();
        }
        searchView.setOnSearchClickListener(this);
        searchView.setMaxWidth((int)(screenWidth*0.85));
        searchView.setOnCloseListener(new onCloseListener());
        searchView.setOnQueryTextListener(new onQuery());
    }

    private void onToggleClick(View view){
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
    }

    private Map<String, Favor> doQuery(String query, Map<String, Favor> searchScope){
        Map<String, Favor> favorsFound = new HashMap<>();
        query = query.toLowerCase();
        for(Favor favor : searchScope.values()){
            if(favor.getTitle().toLowerCase().contains(query) || favor.getDescription().toLowerCase().contains(query))
                favorsFound.put(favor.getId(),favor);
        }
        return favorsFound;
    }

    private void setupSearchMode(){
        displayFavorList(favorsFound, R.string.empty);
        ((MainActivity)(Objects.requireNonNull(getActivity()))).onBackPressedListener =
                () -> { searchView.setIconified(true);
                    if(getView()!=null) CommonTools.hideKeyboardFrom(getContext(), getView()); };
    }

    private void quitSearchMode(){
        favorsFound.clear();
        displayFavorList(nearbyFavors, R.string.favor_no_active_favor);
        ((MainActivity)(Objects.requireNonNull(getActivity()))).onBackPressedListener = null;
        ((MainActivity) Objects.requireNonNull(getActivity())).showBottomTabs();
    }

    private class onCloseListener implements SearchView.OnCloseListener{

        @Override
        public boolean onClose() {
            // clear last query results and recover last listView
            quitSearchMode();
            return false;
        }
    }

    private class onQuery implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            favorsFound = doQuery(query, nearbyFavors);
            displayFavorList(favorsFound, R.string.query_failed);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // replace irrelevant items on listView with last query results or empty view
            if (first) first = false;
            else displayFavorList(new HashMap<String, Favor>(), R.string.empty);
            return false;
        }
    }


    private void setupListView() {
        listView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    ((MainActivity)(Objects.requireNonNull(getActivity()))).onBackPressedListener = null;
                    Favor favor = (Favor) parent.getItemAtPosition(position);
                    CommonTools.replaceFragment(
                            R.id.nav_host_fragment,
                            getParentFragmentManager(),
                            FavorFragmentFactory.instantiate(favor,new FavorDetailView()));
                });
    }


    @Override
    public void onClick(View view) {
        first = false;
        if (view.getId() == R.id.nearby_searchView) {
            setupSearchMode();
        }
    }

    private void displayFavorList(Map<String, Favor> favors, int textId) {
        if (favors.isEmpty()) showText((getString(textId)));
        else tipTextView.setVisibility(View.INVISIBLE);
        listView.setAdapter(new FavorAdapter(getContext(), new ArrayList<>(favors.values())));
    }

    private void showText(String text) {
        tipTextView.setText(text);
        tipTextView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupView(View view) {
        ((ViewController) Objects.requireNonNull(getActivity())).setupViewTopDestTab();
        ((ViewController) Objects.requireNonNull(getActivity())).checkMapViewButton();
        // ensure click on view will hide keyboard
        assert view != null;
        view.findViewById(R.id.constraint_layout_nearby_favor_view)
                .setOnTouchListener(
                        (v, event) -> {
                            hideKeyboardFrom(Objects.requireNonNull(getContext()), v);
                            return false;
                        });
    }
}

