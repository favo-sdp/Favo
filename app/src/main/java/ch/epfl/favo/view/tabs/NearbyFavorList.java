package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.favorList.FavorAdapter;
import ch.epfl.favo.viewmodel.FavorDataController;

import static androidx.navigation.Navigation.findNavController;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */

public class NearbyFavorList extends Fragment {

  private TextView tipTextView;
  private ListView listView;
  private SearchView searchView;
  private MenuItem searchMenuItem;
  private View rootView;
  private Map<String, Favor> favorsFound = new HashMap<>();
  private Map<String, Favor> nearbyFavors;
  private FavorDataController viewModel;

  public NearbyFavorList() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_nearby_favor_list, container, false);
    setupView(rootView);

    tipTextView = rootView.findViewById(R.id.nearby_tip);
    tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

    listView = rootView.findViewById(R.id.nearby_favor_list);
    setupListView();

    RadioButton toggle = rootView.findViewById(R.id.map_switch);
    toggle.setOnClickListener(this::onToggleClick);

    viewModel =
        (FavorDataController)
            new ViewModelProvider(requireActivity()).get(DependencyFactory.getCurrentViewModelClass());

    return rootView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupNearbyFavorsListener();
  }

  public void
      setupNearbyFavorsListener() { // TODO: figure out a way to share view model without using main
                                    // activity life cycle
//    String setting =
//        requireActivity().getPreferences(Context.MODE_PRIVATE).getString("radius", "10 Km");
//    double radius = Double.parseDouble(setting.split(" ")[0]);
    getViewModel()
        .getFavorsAroundMe()
        .observe(
            getViewLifecycleOwner(),
            stringFavorMap -> {
              try {
                nearbyFavors = stringFavorMap;
                displayFavorList(nearbyFavors, R.string.empty);
              } catch (Exception e) {
                CommonTools.showSnackbar(rootView, getString(R.string.error_database_sync));
              }
            });
  }



  public FavorDataController getViewModel() {
    return viewModel;
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {

    // Inflate the menu; this adds items to the action bar if it is present.
    inflater.inflate(R.menu.options_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);

    searchMenuItem = menu.findItem(R.id.search_item);
    searchView = (androidx.appcompat.widget.SearchView) searchMenuItem.getActionView();
    setupSearchListeners();
  }

  private void onToggleClick(View view) {
    findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack(R.id.nav_map, false);
  }

  private void setupSearchMode() {
    rootView.findViewById(R.id.toggle).setVisibility(View.INVISIBLE);
    searchView.setOnQueryTextListener(new onQueryListener());
    displayFavorList(favorsFound, R.string.empty);
  }

  private void quitSearchMode() {
    rootView.findViewById(R.id.toggle).setVisibility(View.VISIBLE);
    favorsFound.clear();
    searchView.setOnQueryTextListener(null);
    displayFavorList(nearbyFavors, R.string.empty);
  }

  private void displayFavorList(Map<String, Favor> favors, int textId) {
    if (favors.isEmpty()) {
      tipTextView.setText(getString(textId));
      tipTextView.setVisibility(View.VISIBLE);
    } else tipTextView.setVisibility(View.INVISIBLE);
    listView.setAdapter(new FavorAdapter(getContext(), new ArrayList<>(favors.values())));
  }

  private void setupSearchListeners() {
    searchMenuItem.setOnActionExpandListener(
        new MenuItem.OnActionExpandListener() {
          @Override
          public boolean onMenuItemActionExpand(MenuItem item) {
            setupSearchMode();
            return true;
          }

          @Override
          public boolean onMenuItemActionCollapse(MenuItem item) {
            quitSearchMode();
            return true;
          }
        });
  }

  class onQueryListener implements SearchView.OnQueryTextListener {
    @Override
    public boolean onQueryTextSubmit(String query) {
      favorsFound = CommonTools.findFavorByTitleDescription(query, nearbyFavors);
      displayFavorList(favorsFound, R.string.query_failed);
      return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
      if (newText.equals("")) favorsFound = new HashMap<>();
      else favorsFound = CommonTools.findFavorByTitleDescription(newText, nearbyFavors);
      displayFavorList(favorsFound, R.string.query_failed);
      return false;
    }
  }

  private void setupListView() {
    listView.setOnItemClickListener(
        (parent, view, position, id) -> {
          CommonTools.hideSoftKeyboard(requireActivity());
          Favor favor = (Favor) parent.getItemAtPosition(position);
          Bundle favorBundle = new Bundle();
          favorBundle.putString("FAVOR_ARGS", favor.getId());
          findNavController(requireView())
              .navigate(R.id.action_nav_nearby_list_to_favorDetailView, favorBundle);
          searchView.onActionViewCollapsed();
        });
  }

  @SuppressLint("ClickableViewAccessibility")
  private void setupView(View view) {
    // ensure click on view will hide keyboard
    view.findViewById(R.id.constraint_layout_nearby_favor_view)
        .setOnTouchListener(
            (v, event) -> {
              CommonTools.hideSoftKeyboard(requireActivity());
              return false;
            });
  }

}

