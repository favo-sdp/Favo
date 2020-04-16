//package ch.epfl.favo.view.tabs;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.SearchView;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.Navigation;
//import androidx.paging.PagedList;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
//import com.firebase.ui.firestore.paging.FirestorePagingOptions;
//import com.firebase.ui.firestore.paging.LoadingState;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//
//import ch.epfl.favo.MainActivity;
//import ch.epfl.favo.R;
//import ch.epfl.favo.favor.Favor;
//import ch.epfl.favo.user.UserUtil;
//import ch.epfl.favo.util.DependencyFactory;
//import ch.epfl.favo.view.tabs.favorList.FavorViewHolder;
//
//import static ch.epfl.favo.favor.Favor.Status.CANCELLED_REQUESTER;
//import static ch.epfl.favo.favor.Favor.Status.REQUESTED;
//import static ch.epfl.favo.util.CommonTools.hideKeyboardFrom;
//
///**
// * View will contain list of favors requested in the past. The list will contain clickable items
// * that will expand to give more information about them. This object is a simple {@link Fragment}
// * subclass.
// */
//public class FavorPage extends Fragment {
//
//  private TextView tipTextView;
//  private ListView listView;
//  private SearchView searchView;
//  private MenuItem spinnerItem;
//
//  private RecyclerView mRecycler;
//  private SwipeRefreshLayout mSwipeRefreshLayout;
//
//  private FirestorePagingAdapter<Favor, FavorViewHolder> activeFavorsAdapter;
//  private FirestorePagingAdapter<Favor, FavorViewHolder> archivedFavorsAdapter;
//
//  private static Query baseQuery =
//      FirebaseFirestore.getInstance()
//          .collection("favors")
//          .orderBy("postedTime", Query.Direction.ASCENDING);
//
//  public FavorPage() {
//    // Required empty public constructor
//  }
//
//  @Override
//  public void onCreate(Bundle bundle) {
//    super.onCreate(bundle);
//    setHasOptionsMenu(true);
//  }
//
//  @Override
//  public View onCreateView(
//      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//    View rootView = inflater.inflate(R.layout.fragment_favorpage, container, false);
//
//    tipTextView = rootView.findViewById(R.id.tip);
//    listView = rootView.findViewById(R.id.favor_list);
//
//    mRecycler = rootView.findViewById(R.id.paging_recycler);
//    mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
//
//    baseQuery =
//        baseQuery.whereEqualTo("requesterId", DependencyFactory.getCurrentFirebaseUser().getUid());
//    activeFavorsAdapter =
//        createPagingAdapterFromQuery(baseQuery.whereEqualTo("statusId", REQUESTED));
//    archivedFavorsAdapter =
//        createPagingAdapterFromQuery(baseQuery.whereEqualTo("statusId", CANCELLED_REQUESTER));
//
//    mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
//    setUpAdapter(activeFavorsAdapter);
//
//    setupListView();
//    setupView();
//    return rootView;
//  }
//
//  private void setUpAdapter(FirestorePagingAdapter<Favor, FavorViewHolder> adapter) {
//    mRecycler.setAdapter(adapter);
//    mSwipeRefreshLayout.setOnRefreshListener(adapter::refresh);
//  }
//
//  private FirestorePagingAdapter<Favor, FavorViewHolder> createPagingAdapterFromQuery(Query query) {
//    FirestorePagingOptions<Favor> options = getFirestorePagingOptions(query);
//    return getFirestorePagingAdapter(options);
//  }
//
//  private FirestorePagingAdapter<Favor, FavorViewHolder> getFirestorePagingAdapter(
//      FirestorePagingOptions<Favor> options) {
//    return new FirestorePagingAdapter<Favor, FavorViewHolder>(options) {
//      @NonNull
//      @Override
//      public FavorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view =
//            LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.favor_list_item, parent, false);
//
//        view.setOnClickListener(
//            v -> {
//              int itemPosition = mRecycler.getChildLayoutPosition(view);
//              DocumentSnapshot doc = getItem(itemPosition);
//              if (doc != null && doc.exists()) {
//                Favor favor = doc.toObject(Favor.class);
//                if (favor != null) {
//                  Bundle favorBundle = new Bundle();
//                  favorBundle.putParcelable("FAVOR_ARGS", favor);
//                  // if favor was requested, open request view
//                  if (favor.getRequesterId().equals(UserUtil.currentUserId)) {
//                    Navigation.findNavController(requireView())
//                        .navigate(R.id.action_nav_favorList_to_favorRequestView, favorBundle);
//                  } else { // if favor was accepted, open accept view
//                    Navigation.findNavController(requireView())
//                        .navigate(R.id.action_nav_favorlist_to_favorDetailView, favorBundle);
//                  }
//                }
//              }
//            });
//
//        return new FavorViewHolder(view);
//      }
//
//      @Override
//      protected void onBindViewHolder(
//          @NonNull FavorViewHolder holder, int position, @NonNull Favor model) {
//        holder.bind(model);
//      }
//
//      @Override
//      protected void onLoadingStateChanged(@NonNull LoadingState state) {
//        mSwipeRefreshLayout.setRefreshing(false);
//        if (state == LoadingState.ERROR) {
//          showToast("An error occurred.");
//          retry();
//        }
//      }
//
//      @Override
//      protected void onError(@NonNull Exception e) {
//        mSwipeRefreshLayout.setRefreshing(false);
//        Log.e("FavorsPage", e.getMessage(), e);
//      }
//    };
//  }
//
//  private FirestorePagingOptions<Favor> getFirestorePagingOptions(Query baseQuery) {
//
//    PagedList.Config config =
//        new PagedList.Config.Builder()
//            .setEnablePlaceholders(false)
//            .setPrefetchDistance(10)
//            .setPageSize(20)
//            .build();
//
//    return new FirestorePagingOptions.Builder<Favor>()
//        .setLifecycleOwner(this)
//        .setQuery(baseQuery, config, Favor.class)
//        .build();
//  }
//
//  private void setupSearchMode() {
//    spinnerItem.setVisible(false);
//    FirestorePagingAdapter<Favor, FavorViewHolder> adapterSearch =
//        createPagingAdapterFromQuery(baseQuery.whereEqualTo("title", ""));
//    displayFavorList(adapterSearch, R.string.empty);
//    ((MainActivity) (requireActivity())).hideBottomNavigation();
//  }
//
//  private void quitSearchMode() {
//    // favorsFound.clear();
//    spinnerItem.setVisible(true);
//    ((MainActivity) (requireActivity())).showBottomNavigation();
//    //    if (lastPosition == 0) displayFavorList(activeFavorsAdapter,
//    // R.string.favor_no_active_favor);
//    //    else displayFavorList(archivedFavorsAdapter, R.string.favor_no_archived_favor);
//  }
//
//  private void displayFavorList(
//      FirestorePagingAdapter<Favor, FavorViewHolder> adapter, int textId) {
//    if (adapter.getItemCount() == 0) showText((getString(textId)));
//    else tipTextView.setVisibility(View.INVISIBLE);
//    mRecycler.setAdapter(adapter);
//  }
//
//  private void showText(String text) {
//    tipTextView.setText(text);
//    tipTextView.setVisibility(View.VISIBLE);
//  }
//
//  @SuppressLint("ClickableViewAccessibility")
//  private void setupView() {
//    // ensure click on view will hide keyboard
//    listView.setOnTouchListener(
//        (v, event) -> {
//          hideKeyboardFrom(requireContext(), v);
//          return false;
//        });
//  }
//
//  @Override
//  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//    // Inflate the menu; this adds items to the action bar if it is present.
//    inflater.inflate(R.menu.options_menu, menu);
//    super.onCreateOptionsMenu(menu, inflater);
//
//    setupSpinner(menu);
//    setupSearch(menu);
//  }
//
//  private void setupSearch(Menu menu) {
//    MenuItem searchMenuItem = menu.findItem(R.id.search_item);
//    searchView = (SearchView) searchMenuItem.getActionView();
//
//    setupSearchListeners(searchMenuItem);
//  }
//
//  private void setupSearchListeners(MenuItem searchMenuItem) {
//    // replacing the other two callbacks because they were buggy according to some stack overflow
//    // forums
//    searchMenuItem.setOnActionExpandListener(
//        new MenuItem.OnActionExpandListener() {
//
//          @Override
//          public boolean onMenuItemActionExpand(MenuItem item) {
//            setupSearchMode();
//            return true;
//          }
//
//          @Override
//          public boolean onMenuItemActionCollapse(MenuItem item) {
//            quitSearchMode();
//            return true;
//          }
//        });
//
//    searchView.setOnQueryTextListener(
//        new SearchView.OnQueryTextListener() {
//          @Override
//          public boolean onQueryTextSubmit(String query) {
//            return false;
//          }
//
//          @Override
//          public boolean onQueryTextChange(String newText) {
//            // replace irrelevant items on listView with last query results or empty view
//            FirestorePagingAdapter<Favor, FavorViewHolder> adapterSearch =
//                createPagingAdapterFromQuery(baseQuery.whereEqualTo("title", newText));
//            displayFavorList(adapterSearch, R.string.query_failed);
//            return true;
//          }
//        });
//  }
//
//  private void setupSpinner(Menu menu) {
//    spinnerItem = menu.findItem(R.id.spinner);
//    Spinner spinner = (Spinner) spinnerItem.getActionView();
//
//    ArrayAdapter<CharSequence> adapter =
//        ArrayAdapter.createFromResource(
//            requireContext(), R.array.favor_list_spinner, android.R.layout.simple_spinner_item);
//    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//    spinner.setAdapter(adapter);
//
//    setupSpinnerListeners(spinner);
//  }
//
//  private void setupSpinnerListeners(Spinner spinner) {
//    spinner.setOnItemSelectedListener(
//        new AdapterView.OnItemSelectedListener() {
//          @Override
//          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            if (position == 0) {
//              displayFavorList(activeFavorsAdapter, R.string.favor_no_active_favor);
//            } else {
//              displayFavorList(archivedFavorsAdapter, R.string.favor_no_archived_favor);
//            }
//          }
//
//          @Override
//          public void onNothingSelected(AdapterView<?> parent) {}
//        });
//  }
//
//  private void showToast(@NonNull String message) {
//    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//  }
//}
