package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.view.tabs.favorList.searchBarCoordinator;

import static ch.epfl.favo.util.CommonTools.hideKeyboardFrom;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */
@SuppressLint("NewApi")
public class FavorPage extends Fragment implements View.OnClickListener {
  private TextView tipTextView;
  private Spinner spinner;
  private ListView listView;
  private SearchView searchView;
  private int lastPosition;
  MainActivity activity;
  private searchBarCoordinator searchBarCoordinator;

  public FavorPage() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    // Extract two arrayLists from the main activity
    activity = (MainActivity) Objects.requireNonNull(getActivity());
    searchBarCoordinator = new searchBarCoordinator(activity, getContext(), "ActiveList");
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_favorpage, container, false);
    rootView.findViewById(R.id.floatingActionButton).setOnClickListener(this);

    tipTextView = rootView.findViewById(R.id.tip);
    tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

    spinner = rootView.findViewById(R.id.spinner);
    listView = rootView.findViewById(R.id.favor_list);
    searchView = rootView.findViewById(R.id.searchView);
    searchView.setOnCloseListener(()->{quitSearchMode();return false;});
    searchBarCoordinator.setupAssets(tipTextView, listView, searchView);
    searchBarCoordinator.setupSearchBar(rootView);
    setupListView();
    setupSpinner();
    setupView();
    return rootView;
  }


  private void quitSearchMode() {
    searchBarCoordinator.clearFoundFavors();
    activity.onBackPressedListener = null;
    activity.showBottomTabs();
    spinner.setVisibility(View.VISIBLE);
    if (lastPosition == 0) searchBarCoordinator.displayContent(getString(R.string.favor_no_active_favor), 0);
    else searchBarCoordinator.displayContent(getString(R.string.favor_no_archived_favor), 1);
  }


  private void setupListView() {
    listView.setOnItemClickListener(
        (parent, view, position, id) -> {
          activity.onBackPressedListener = null;
          CommonTools.hideKeyboardFrom(Objects.requireNonNull(getContext()), view);
          Favor favor = (Favor) parent.getItemAtPosition(position);
          Bundle favorBundle = new Bundle();
          favorBundle.putParcelable("FAVOR_ARGS", favor);
          // if favor was requested, open request view
          if (favor.getRequesterId().equals(UserUtil.currentUserId)) {
            Navigation.findNavController(getView())
                .navigate(R.id.action_nav_favorlist_to_favorRequestView, favorBundle);
          } else { // if favor was accepted, open accept view
            Navigation.findNavController(getView())
                .navigate(R.id.action_nav_favorlist_to_favorDetailView, favorBundle);
          }
        });
  }

  private void setupSpinner() {
    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
              lastPosition = 0;
              searchBarCoordinator.displayContent(getString(R.string.favor_no_active_favor), 0);
            } else {
              lastPosition = 1;
              searchBarCoordinator.displayContent(getString(R.string.favor_no_archived_favor), 1);
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.floatingActionButton)
      Navigation.findNavController(view).navigate(R.id.action_nav_favorlist_to_favorRequestView);
  }

  @SuppressLint("ClickableViewAccessibility")
  private void setupView() {
    activity.setupViewTopDestTab();
    activity.checkFavListViewButton();
    // ensure click on view will hide keyboard
    listView.setOnTouchListener(
        (v, event) -> {
          hideKeyboardFrom(Objects.requireNonNull(getContext()), v);
          return false;
        });
  }
}
