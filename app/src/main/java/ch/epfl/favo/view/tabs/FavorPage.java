package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.view.tabs.favorList.FavorAdapter;

import static ch.epfl.favo.util.CommonTools.hideKeyboardFrom;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */
public class FavorPage extends Fragment {

  private Map<String, Favor> activeFavors;
  private Map<String, Favor> archivedFavors;
  private TextView tipTextView;
  private ListView listView;
  private SearchView searchView;
  private int lastPosition;
  private String lastQuery;
  private Map<String, Favor> favorsFound = new HashMap<>();
  private MenuItem spinnerItem;

  public FavorPage() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    // Extract two arrayLists from the main activity
    MainActivity activity = (MainActivity) Objects.requireNonNull(getActivity());
    activeFavors = activity.activeFavors;
    archivedFavors = activity.archivedFavors;
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_favorpage, container, false);

    tipTextView = rootView.findViewById(R.id.tip);
    listView = rootView.findViewById(R.id.favor_list);

    setupListView();
    setupView();
    return rootView;
  }

  private Map<String, Favor> doQuery(String query, Map<String, Favor> searchScope) {
    Map<String, Favor> favorsFound = new HashMap<>();
    query = query.toLowerCase();
    for (Favor favor : searchScope.values()) {
      if (favor.getTitle().toLowerCase().contains(query)
          || favor.getDescription().toLowerCase().contains(query))
        favorsFound.put(favor.getId(), favor);
    }
    return favorsFound;
  }

  private void setupSearchMode() {
    spinnerItem.setVisible(false);
    displayFavorList(favorsFound, R.string.empty);
    ((MainActivity) (Objects.requireNonNull(getActivity()))).hideBottomNavigation();
  }

  private void quitSearchMode() {
    favorsFound.clear();
    spinnerItem.setVisible(true);
    ((MainActivity) (Objects.requireNonNull(getActivity()))).showBottomNavigation();
    if (lastPosition == 0) displayFavorList(activeFavors, R.string.favor_no_active_favor);
    else displayFavorList(archivedFavors, R.string.favor_no_archived_favor);
  }

  private void setupListView() {
    listView.setOnItemClickListener(
        (parent, view, position, id) -> {
          Favor favor = (Favor) parent.getItemAtPosition(position);
          Bundle favorBundle = new Bundle();
          favorBundle.putParcelable("FAVOR_ARGS", favor);
          // if favor was requested, open request view
          if (favor.getRequesterId().equals(UserUtil.currentUserId)) {
            Navigation.findNavController(Objects.requireNonNull(getView()))
                .navigate(R.id.action_nav_favorList_to_favorRequestView, favorBundle);
          } else { // if favor was accepted, open accept view
            Navigation.findNavController(Objects.requireNonNull(getView()))
                .navigate(R.id.action_nav_favorlist_to_favorDetailView, favorBundle);
          }
        });
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
  private void setupView() {
    // ensure click on view will hide keyboard
    listView.setOnTouchListener(
        (v, event) -> {
          hideKeyboardFrom(Objects.requireNonNull(getContext()), v);
          return false;
        });
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    // Inflate the menu; this adds items to the action bar if it is present.
    inflater.inflate(R.menu.options_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);

    setupSpinner(menu);
    setupSearch(menu);
  }

  private void setupSearch(Menu menu) {
    MenuItem searchMenuItem = menu.findItem(R.id.search_item);
    searchView = (SearchView) searchMenuItem.getActionView();

    // if returned from FavorDetail view, continue to show the search mode

    // commenting for the moment because it doesn't work with androidx, will work on it in the next
    // PR when sync list with database

    //    if (!favorsFound.isEmpty()) {
    //      searchView.setIconified(false);
    //      searchView.clearFocus();
    //      setupSearchMode();
    //    }

    setupSearchListeners(searchMenuItem);
  }

  private void setupSearchListeners(MenuItem searchMenuItem) {
    // replacing the other two callbacks because they were buggy according to some stack overflow
    // forums
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

    searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
            lastQuery = query;
            favorsFound = doQuery(query, activeFavors);
            favorsFound.putAll(doQuery(query, archivedFavors));
            displayFavorList(favorsFound, R.string.query_failed);
            return false;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
            // replace irrelevant items on listView with last query results or empty view

            // displayFavorList(new HashMap<>(), R.string.empty);
            return false;
          }
        });
  }

  private void setupSpinner(Menu menu) {
    spinnerItem = menu.findItem(R.id.spinner);
    Spinner spinner = (Spinner) spinnerItem.getActionView();

    ArrayAdapter<CharSequence> adapter =
        ArrayAdapter.createFromResource(
            Objects.requireNonNull(getContext()),
            R.array.favor_list_spinner,
            android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);

    setupSpinnerListeners(spinner);
  }

  private void setupSpinnerListeners(Spinner spinner) {
    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!favorsFound.isEmpty()) {
              // if stay in search mode, show the last query text and display last query results
              // favorsFound will be automatically cleared if quit from search mode
              searchView.setQuery(lastQuery, false); // this block is unreachable?
              displayFavorList(favorsFound, R.string.query_failed);
            } else if (position == 0) {
              lastPosition = 0;
              displayFavorList(activeFavors, R.string.favor_no_active_favor);
            } else {
              lastPosition = 1;
              displayFavorList(archivedFavors, R.string.favor_no_archived_favor);
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
  }
}
