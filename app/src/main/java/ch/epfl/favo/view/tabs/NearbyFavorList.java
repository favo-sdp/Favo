package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.view.ViewController;
import ch.epfl.favo.view.tabs.favorList.FavorAdapter;

import static androidx.navigation.Navigation.findNavController;
import static ch.epfl.favo.util.CommonTools.hideKeyboardFrom;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */
public class NearbyFavorList extends Fragment implements View.OnClickListener {

    private TextView tipTextView;
    private ListView listView;
    private SearchView searchView;
    private int screenWidth;
    private boolean first = true;
    private Map<String, Favor> favorsFound = new HashMap<>();
    private MainActivity activity;
    private NavController navController;
    public NearbyFavorList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        activity = (MainActivity) Objects.requireNonNull(getActivity());
        navController = findNavController(activity, R.id.nav_host_fragment);
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        first = true;
        View rootView = inflater.inflate(R.layout.fragment_nearby_favor_list, container, false);
        setupView(rootView);

        tipTextView = rootView.findViewById(R.id.nearby_tip);
        tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        listView = rootView.findViewById(R.id.nearby_favor_list);
        setupListView();

        RadioButton toggle = rootView.findViewById(R.id.map_switch);
        toggle.setOnClickListener(this::onToggleClick);

        searchView = rootView.findViewById(R.id.nearby_searchView);
        setupSearchView();
        if(!favorsFound.isEmpty())
            displayFavorList(favorsFound, R.string.query_failed);
        else
            displayFavorList(activity.otherActiveFavorsAround, R.string.favor_no_nearby_favor);
        return rootView;
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
        navController.popBackStack(R.id.nav_map, false);
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
        displayFavorList(activity.otherActiveFavorsAround, R.string.favor_no_active_favor);
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
            favorsFound = doQuery(query, activity.otherActiveFavorsAround);
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
                    Bundle favorBundle = new Bundle();
                    favorBundle.putParcelable("FAVOR_ARGS", favor);
                    Navigation.findNavController(getView())
                            .navigate(R.id.action_nav_nearby_list_to_favorDetailView, favorBundle);
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

