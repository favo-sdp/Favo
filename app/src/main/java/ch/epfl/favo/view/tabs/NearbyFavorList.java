package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.view.ViewController;
import ch.epfl.favo.view.tabs.favorList.searchBarCoordinator;

import static androidx.navigation.Navigation.findNavController;
import static ch.epfl.favo.util.CommonTools.hideKeyboardFrom;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */
public class NearbyFavorList extends Fragment{

    private TextView tipTextView;
    private ListView listView;
    private SearchView searchView;
    private searchBarCoordinator searchBarCoordinator;
    private MainActivity activity;
    public NearbyFavorList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activity = (MainActivity) Objects.requireNonNull(getActivity());
        searchBarCoordinator = new searchBarCoordinator(activity, getContext(), "NearbyList");
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nearby_favor_list, container, false);
        setupView(rootView);

        tipTextView = rootView.findViewById(R.id.nearby_tip);
        tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        listView = rootView.findViewById(R.id.nearby_favor_list);
        setupListView();

        RadioButton toggle = rootView.findViewById(R.id.map_switch);
        toggle.setOnClickListener(this::onToggleClick);
        searchView = rootView.findViewById(R.id.nearby_searchView);
        searchView.setOnCloseListener(()->{quitSearchMode();return false;});
        searchBarCoordinator.setupAssets(tipTextView, listView, searchView);
        searchBarCoordinator.setupSearchBar(rootView);
        searchBarCoordinator.displayContent(getString(R.string.favor_no_nearby_favor), 0);
        return rootView;
    }

    private void onToggleClick(View view){
        findNavController(activity, R.id.nav_host_fragment).popBackStack(R.id.nav_map, false);
    }


    private void quitSearchMode(){
        searchBarCoordinator.clearFoundFavors();
        searchBarCoordinator.displayContent(getString(R.string.favor_no_nearby_favor), 0);
        ((MainActivity)(Objects.requireNonNull(getActivity()))).onBackPressedListener = null;
        ((MainActivity) Objects.requireNonNull(getActivity())).showBottomTabs();
    }


    private void setupListView() {
        listView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    activity.onBackPressedListener = null;
                    CommonTools.hideKeyboardFrom(Objects.requireNonNull(getContext()), view);
                    Favor favor = (Favor) parent.getItemAtPosition(position);
                    Bundle favorBundle = new Bundle();
                    favorBundle.putParcelable("FAVOR_ARGS", favor);
                    Navigation.findNavController(getView())
                            .navigate(R.id.action_nav_nearby_list_to_favorDetailView, favorBundle);
                });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupView(View view) {
        ((ViewController) Objects.requireNonNull(getActivity())).setupViewTopDestTab();
        ((ViewController) Objects.requireNonNull(getActivity())).checkMapViewButton();
        // ensure click on view will hide keyboard
        view.findViewById(R.id.constraint_layout_nearby_favor_view).setOnTouchListener(
                        (v, event) -> {
                            hideKeyboardFrom(Objects.requireNonNull(getContext()), v);
                            return false; });
    }
}

