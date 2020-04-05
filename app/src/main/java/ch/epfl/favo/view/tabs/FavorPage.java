package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.FavorFragmentFactory;
import ch.epfl.favo.view.ViewController;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;
import ch.epfl.favo.view.tabs.favorList.FavorAdapter;

import static ch.epfl.favo.util.CommonTools.hideKeyboardFrom;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */
public class FavorPage extends Fragment implements View.OnClickListener {

  private Map<String, Favor> activeFavors;
  private Map<String, Favor> archivedFavors;
  private TextView tipTextView;
  private Spinner spinner;
  private ListView listView;
  private SearchView searchView;
  private int lastPosition;
  private String lastQuery;
  private Map<String, Favor> favorsFound = new HashMap<>();

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
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_favorpage, container, false);
    rootView.findViewById(R.id.floatingActionButton).setOnClickListener(this);
    setupView(rootView);
    tipTextView = rootView.findViewById(R.id.tip);
    tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

    spinner = rootView.findViewById(R.id.spinner);
    listView = rootView.findViewById(R.id.favor_list);
    searchView = rootView.findViewById(R.id.searchView);
    setupListView();
    setupSpinner();
    setupSearchView();
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
    searchView.setMaxWidth(600);
    searchView.setOnCloseListener(new onCloseListener());
    searchView.setOnQueryTextListener(new onQuery());
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
    spinner.setVisibility(View.INVISIBLE);
    displayFavorList(favorsFound, R.string.empty);
    ((MainActivity)(Objects.requireNonNull(getActivity()))).onBackPressedListener =
            () -> { searchView.setIconified(true);
              if(getView()!=null) CommonTools.hideKeyboardFrom(getContext(), getView()); };
  }

  private void quitSearchMode(){
    favorsFound.clear();
    ((MainActivity)(Objects.requireNonNull(getActivity()))).onBackPressedListener = null;
    spinner.setVisibility(View.VISIBLE);
    ((MainActivity) Objects.requireNonNull(getActivity())).showBottomTabs();
    if(lastPosition == 0)
      displayFavorList(activeFavors, R.string.favor_no_active_favor);
    else
      displayFavorList(archivedFavors, R.string.favor_no_archived_favor);
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
      lastQuery = query;
      favorsFound = doQuery(query, activeFavors);
      favorsFound.putAll(doQuery(query, archivedFavors));
      displayFavorList(favorsFound, R.string.query_failed);
      return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
      // replace irrelevant items on listView with last query results or empty view
      displayFavorList(new HashMap<String, Favor>(), R.string.empty);
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
                      FavorFragmentFactory.instantiate(favor,new FavorRequestView()));
            });
  }

  private void setupSpinner() {
    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(!favorsFound.isEmpty()){
              // if stay in search mode, show the last query text and display last query results
              // favorsFound will be automatically cleared if quit from search mode
              searchView.setQuery(lastQuery, false);
              displayFavorList(favorsFound, R.string.query_failed);
            }
            else if (position == 0) {
              lastPosition = 0;
              displayFavorList(activeFavors, R.string.favor_no_active_favor);
            } else{
              lastPosition = 1;
              displayFavorList(archivedFavors, R.string.favor_no_archived_favor);
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
  }

  @Override
  public void onClick(View view) {

    switch (view.getId()) {
      case R.id.floatingActionButton:
        CommonTools.replaceFragment(
            R.id.nav_host_fragment, getParentFragmentManager(), new FavorRequestView());
        break;
      case R.id.searchView:
        setupSearchMode();
        break;
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
    ((ViewController) Objects.requireNonNull(getActivity())).checkFavListViewButton();
    // ensure click on view will hide keyboard
    assert view != null;
    view.findViewById(R.id.constraint_layout_favor_view)
            .setOnTouchListener(
                    (v, event) -> {
                      hideKeyboardFrom(Objects.requireNonNull(getContext()), v);
                      return false;
                    });
  }
}
