package ch.epfl.favo.view.tabs.favorList;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.FavorFragmentFactory;
import ch.epfl.favo.view.ViewController;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;

import static ch.epfl.favo.favor.Favor.Status.CANCELLED_REQUESTER;
import static ch.epfl.favo.favor.Favor.Status.REQUESTED;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */
public class FavorPage extends Fragment implements View.OnClickListener {

  //  private Map<String, Favor> activeFavors;
  //  private Map<String, Favor> archivedFavors;
  private TextView tipTextView;
  private Spinner spinner;
  // private ListView listView;
  private SearchView searchView;
  private int lastPosition;
  //private String lastQuery;
  private int screenWidth;
  // private Map<String, Favor> favorsFound = new HashMap<>();

  private RecyclerView mRecycler;
  private SwipeRefreshLayout mSwipeRefreshLayout;

  private FirestorePagingAdapter<Favor, FavorViewHolder> activeFavorsAdapter;
  private FirestorePagingAdapter<Favor, FavorViewHolder> archivedFavorsAdapter;

  private static Query baseQuery =
      FirebaseFirestore.getInstance()
          .collection("favors")
          .orderBy("postedTime", Query.Direction.ASCENDING);

  public FavorPage() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    // Extract two arrayLists from the main activity
    MainActivity activity = (MainActivity) Objects.requireNonNull(getActivity());
    //      activeFavors = activity.activeFavors;
    //      archivedFavors = activity.archivedFavors;
    Display display = activity.getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    screenWidth = size.x;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_favorpage, container, false);
    setupView();

    rootView.findViewById(R.id.floatingActionButton).setOnClickListener(this);

    tipTextView = rootView.findViewById(R.id.tip);
    tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

    spinner = rootView.findViewById(R.id.spinner);
    // listView = rootView.findViewById(R.id.favor_list);
    searchView = rootView.findViewById(R.id.searchView);

    mRecycler = rootView.findViewById(R.id.paging_recycler);
    mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);

    baseQuery =
        baseQuery.whereEqualTo("requesterId", DependencyFactory.getCurrentFirebaseUser().getUid());
    activeFavorsAdapter =
        createPagingAdapterFromQuery(baseQuery.whereEqualTo("statusId", REQUESTED));
    archivedFavorsAdapter =
        createPagingAdapterFromQuery(baseQuery.whereEqualTo("statusId", CANCELLED_REQUESTER));

    mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    setUpAdapter(activeFavorsAdapter);

    // setupListView();
    setupSpinner();
    setupSearchView();
    return rootView;
  }

  private void setUpAdapter(FirestorePagingAdapter<Favor, FavorViewHolder> adapter) {
    mRecycler.setAdapter(adapter);
    mSwipeRefreshLayout.setOnRefreshListener(adapter::refresh);
  }

  private FirestorePagingAdapter<Favor, FavorViewHolder> createPagingAdapterFromQuery(Query query) {
    FirestorePagingOptions<Favor> options = getFirestorePagingOptions(query);
    return getFirestorePagingAdapter(options);
  }

  private FirestorePagingAdapter<Favor, FavorViewHolder> getFirestorePagingAdapter(
      FirestorePagingOptions<Favor> options) {
    return new FirestorePagingAdapter<Favor, FavorViewHolder>(options) {
      @NonNull
      @Override
      public FavorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favor_list_item, parent, false);

        view.setOnClickListener(
            v -> {
              ((MainActivity) (Objects.requireNonNull(getActivity()))).onBackPressedListener = null;
              int itemPosition = mRecycler.getChildLayoutPosition(view);
              DocumentSnapshot doc = getItem(itemPosition);
              if (doc != null && doc.exists()) {
                Favor favor = doc.toObject(Favor.class);
                CommonTools.replaceFragment(
                    R.id.nav_host_fragment,
                    getParentFragmentManager(),
                    FavorFragmentFactory.instantiate(favor, new FavorRequestView()));
              }
            });

        return new FavorViewHolder(view);
      }

      @Override
      protected void onBindViewHolder(
          @NonNull FavorViewHolder holder, int position, @NonNull Favor model) {
        holder.bind(model);
      }

      @Override
      protected void onLoadingStateChanged(@NonNull LoadingState state) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (state == LoadingState.ERROR) {
          showToast("An error occurred.");
          retry();
        }
      }

      @Override
      protected void onError(@NonNull Exception e) {
        mSwipeRefreshLayout.setRefreshing(false);
        Log.e("FavorsPage", e.getMessage(), e);
      }
    };
  }

  private FirestorePagingOptions<Favor> getFirestorePagingOptions(Query baseQuery) {

    PagedList.Config config =
        new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(20)
            .build();

    return new FirestorePagingOptions.Builder<Favor>()
        .setLifecycleOwner(this)
        .setQuery(baseQuery, config, Favor.class)
        .build();
  }

  private void setupSearchView() {
    searchView.setIconifiedByDefault(true);
    searchView.setTag("SearchView");
    // if returned from FavorDetail view, continue to show the search mode
//    if (!favorsFound.isEmpty()) {
//      searchView.setIconified(false);
//      searchView.clearFocus();
//      setupSearchMode();
//    }
    searchView.setOnSearchClickListener(this);
    searchView.setMaxWidth((int) (screenWidth * 0.85));
    searchView.setOnCloseListener(new onCloseListener());
    searchView.setOnQueryTextListener(new onQuery());
  }

  //  private Map<String, Favor> doQuery(String query, Map<String, Favor> searchScope) {
  //    Map<String, Favor> favorsFound = new HashMap<>();
  //    query = query.toLowerCase();
  //    for (Favor favor : searchScope.values()) {
  //      if (favor.getTitle().toLowerCase().contains(query)
  //          || favor.getDescription().toLowerCase().contains(query))
  //        favorsFound.put(favor.getId(), favor);
  //    }
  //    return favorsFound;
  //  }

  private void setupSearchMode() {
    spinner.setVisibility(View.INVISIBLE);
    FirestorePagingAdapter<Favor, FavorViewHolder> adapterSearch =
        createPagingAdapterFromQuery(baseQuery.whereEqualTo("title", ""));
    displayFavorList(adapterSearch, R.string.empty);
    ((MainActivity) (Objects.requireNonNull(getActivity()))).onBackPressedListener =
        () -> {
          searchView.setIconified(true);
          if (getView() != null)
            CommonTools.hideKeyboardFrom(Objects.requireNonNull(getContext()), getView());
        };
  }

  private void quitSearchMode() {
    // favorsFound.clear();
    ((MainActivity) (Objects.requireNonNull(getActivity()))).onBackPressedListener = null;
    spinner.setVisibility(View.VISIBLE);
    ((MainActivity) Objects.requireNonNull(getActivity())).showBottomTabs();
    if (lastPosition == 0) displayFavorList(activeFavorsAdapter, R.string.favor_no_active_favor);
    else displayFavorList(archivedFavorsAdapter, R.string.favor_no_archived_favor);
  }

  private class onCloseListener implements SearchView.OnCloseListener {

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
      //      lastQuery = query;
      //      favorsFound = doQuery(query, activeFavors);
      //      favorsFound.putAll(doQuery(query, archivedFavors));
      //      displayFavorList(favorsFound, R.string.query_failed);
      return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
      // replace irrelevant items on listView with last query results or empty view
      FirestorePagingAdapter<Favor, FavorViewHolder> adapterSearch =
          createPagingAdapterFromQuery(baseQuery.whereEqualTo("title", newText));
      displayFavorList(adapterSearch, R.string.query_failed);
      return true;
    }
  }

  //  private void setupListView() {
  //    listView.setOnItemClickListener(
  //        (parent, view, position, id) -> {
  //          ((MainActivity) (Objects.requireNonNull(getActivity()))).onBackPressedListener = null;
  //          Favor favor = (Favor) parent.getItemAtPosition(position);
  //          CommonTools.replaceFragment(
  //              R.id.nav_host_fragment,
  //              getParentFragmentManager(),
  //              FavorFragmentFactory.instantiate(favor, new FavorRequestView()));
  //        });
  //  }

  private void setupSpinner() {
    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            if (!favorsFound.isEmpty()) {
//              // if stay in search mode, show the last query text and display last
//              // query results
//              // favorsFound will be automatically cleared if quit from search mode
//              searchView.setQuery(lastQuery, false);
//              displayFavorList(favorsFound, R.string.query_failed);
            //} else
              if (position == 0) {
              lastPosition = 0;
              displayFavorList(activeFavorsAdapter, R.string.favor_no_active_favor);
            } else {
              lastPosition = 1;
              displayFavorList(archivedFavorsAdapter, R.string.favor_no_archived_favor);
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

  private void displayFavorList(
      FirestorePagingAdapter<Favor, FavorViewHolder> adapter, int textId) {
    if (adapter.getItemCount() == 0) showText((getString(textId)));
    else tipTextView.setVisibility(View.INVISIBLE);
    mRecycler.setAdapter(adapter);
  }

  private void showText(String text) {
    tipTextView.setText(text);
    tipTextView.setVisibility(View.VISIBLE);
  }

  private void setupView() {
    ((ViewController) Objects.requireNonNull(getActivity())).setupViewTopDestTab();
    ((ViewController) Objects.requireNonNull(getActivity())).checkFavListViewButton();
    //    // ensure click on view will hide keyboard
    //    view.findViewById(R.id.constraint_layout_favor_view)
    //        .setOnTouchListener(
    //            (v, event) -> {
    //              hideKeyboardFrom(Objects.requireNonNull(getContext()), v);
    //              return false;
    //            });
  }

  private void showToast(@NonNull String message) {
    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
  }
}
