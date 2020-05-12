package ch.epfl.favo.view.tabs.addFavor;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.exception.IllegalRequestException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.NonClickableToolbar;
import ch.epfl.favo.viewmodel.IFavorViewModel;

import static androidx.navigation.Navigation.findNavController;

@SuppressLint("NewApi")
public class FavorPublishedView extends Fragment {
  private static String TAG = "FavorPublishedView";
  private FavorStatus favorStatus;
  private Favor currentFavor;
  private Button acceptAndCompleteFavorBtn;
  private Button chatBtn;
  private IFavorViewModel favorViewModel;
  private NonClickableToolbar toolbar;
  private MenuItem cancelItem;
  private MenuItem editItem;
  private MenuItem restartItem;
  private MenuItem inviteItem;
  private MenuItem deleteItem;
  private MenuItem cancelCommitItem;
  private boolean isRequested;

  private Map<String, User> commitUsers = new HashMap<>();

  public FavorPublishedView() {
    // create favor detail from a favor
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {

    // Inflate the menu; this adds items to the action bar if it is present.
    inflater.inflate(R.menu.favor_published_menu, menu);

    cancelItem = menu.findItem(R.id.cancel_button);
    cancelCommitItem = menu.findItem(R.id.cancel_commit_button);
    editItem = menu.findItem(R.id.edit_button);
    restartItem = menu.findItem(R.id.restart_button);
    inviteItem = menu.findItem(R.id.share_button);
    deleteItem = menu.findItem(R.id.delete_button);

    // if (favorStatus != null) {
    //  updateAcceptBtnDisplay();
    // }
    super.onCreateOptionsMenu(menu, inflater);
  }

  // handle button activities
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.cancel_button:
        cancelFavor();
        break;
      case R.id.cancel_commit_button:
        cancelCommit();
        break;
      case R.id.edit_button:
        goEditFavor();
        break;
      case R.id.share_button:
        onShareClicked();
        break;
      case R.id.restart_button:
        goRestartFavor();
        break;
      case R.id.delete_button:
        deleteFavor();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // inflate view
    View rootView = inflater.inflate(R.layout.fragment_favor_published_view, container, false);
    setupButtons(rootView);

    toolbar = requireActivity().findViewById(R.id.toolbar_main_activity);
    favorViewModel =
        (IFavorViewModel)
            new ViewModelProvider(requireActivity())
                .get(DependencyFactory.getCurrentViewModelClass());
    String favorId = "";
    if (currentFavor != null) favorId = currentFavor.getId();
    if (getArguments() != null) favorId = getArguments().getString(CommonTools.FAVOR_ARGS);
    setupFavorListener(rootView, favorId);
    return rootView;
  }

  public IFavorViewModel getViewModel() {
    return favorViewModel;
  }

  private void setupFavorListener(View rootView, String favorId) {
    getViewModel()
        .setObservedFavor(favorId)
        .observe(
            getViewLifecycleOwner(),
            favor -> {
              try {
                if (favor != null && favor.getId().equals(favorId)) {
                  currentFavor = favor;
                }
              } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                CommonTools.showSnackbar(rootView, getString(R.string.error_database_sync));
                enableButtons(false);
              }
              if (currentFavor != null) displayFromFavor(rootView, currentFavor);
            });
  }

  private void onShareClicked() {
    Uri baseUrl = Uri.parse(getString(R.string.favoapp_url) + currentFavor.getId());
    String domain = getString(R.string.favoapp_domain);

    DynamicLink link =
        FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            .setDomainUriPrefix(domain)
            .setAndroidParameters(
                new DynamicLink.AndroidParameters.Builder(getString(R.string.favo_package_name))
                    .build())
            .setSocialMetaTagParameters(
                new DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle("Favor " + currentFavor.getTitle())
                    .setDescription(getString(R.string.share_tip_checkout_favor))
                    .build())
            .buildDynamicLink();

    ((MainActivity) requireActivity()).startShareIntent(link.getUri().toString());
  }

  private void updateAppBarMenuDisplay() {
    // if one of the Item is not ready, then all of them are not ready, just return
    if (cancelItem == null) return;
    // if requester has committed this favor, then he can cancel commit
    boolean cancelCommitVisible =
        !isRequested
            && favorStatus == FavorStatus.REQUESTED
            && currentFavor
                .getUserIds()
                .contains(DependencyFactory.getCurrentFirebaseUser().getUid());
    cancelCommitItem.setVisible(cancelCommitVisible);

    boolean cancelVisible =
        (favorStatus == FavorStatus.REQUESTED && isRequested)
            || favorStatus == FavorStatus.ACCEPTED
            || favorStatus == FavorStatus.COMPLETED_ACCEPTER
            || favorStatus == FavorStatus.COMPLETED_REQUESTER;
    cancelItem.setVisible(cancelVisible);

    boolean restartVisible =
        (favorStatus == FavorStatus.CANCELLED_ACCEPTER
            || (favorStatus == FavorStatus.CANCELLED_REQUESTER
                    || favorStatus == FavorStatus.SUCCESSFULLY_COMPLETED)
                && isRequested);
    restartItem.setVisible(restartVisible);

    deleteItem.setVisible(currentFavor.getIsArchived());
    editItem.setVisible(isRequested && favorStatus == FavorStatus.REQUESTED);
    inviteItem.setVisible(favorStatus == FavorStatus.REQUESTED);
  }

  private void setupButtons(View rootView) {
    acceptAndCompleteFavorBtn = rootView.findViewById(R.id.accept_button);
    chatBtn = rootView.findViewById(R.id.chat_button);
    TextView locationAccessBtn = rootView.findViewById(R.id.location);
    ImageView userProfile = rootView.findViewById(R.id.user_profile_picture);
    TextView userName = rootView.findViewById(R.id.user_name);

    locationAccessBtn.setOnClickListener(
        v -> {
          favorViewModel.setShowObservedFavor(true);
          findNavController(requireActivity(), R.id.nav_host_fragment)
              .popBackStack(R.id.nav_map, false);
        });
    // If clicking for the first time, then accept the favor
    acceptAndCompleteFavorBtn.setOnClickListener(
        v -> {
          if (currentFavor.getStatusId() == FavorStatus.REQUESTED.toInt()) commitFavor();
          else completeFavor();
        });

    chatBtn.setOnClickListener(new toUserInfoPage());
    chatBtn.setOnClickListener(
        v -> {
          Bundle favorBundle = new Bundle();
          favorBundle.putParcelable("FAVOR_ARGS", currentFavor);
          Navigation.findNavController(requireView())
              .navigate(R.id.action_nav_favorPublishedView_to_chatView, favorBundle);
        });
    userProfile.setOnClickListener(new toUserInfoPage());
    userName.setOnClickListener(new toUserInfoPage());
  }

  class toUserInfoPage implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      Bundle userBundle = new Bundle();
      userBundle.putString(CommonTools.USER_ARGS, currentFavor.getRequesterId());
      Navigation.findNavController(requireView())
          .navigate(R.id.action_nav_favorPublishedView_to_UserInfoPage, userBundle);
    }
  }

  private void displayFromFavor(View rootView, Favor favor) {

    String timeStr = CommonTools.convertTime(favor.getLocation().getTime());
    String titleStr = favor.getTitle();
    String descriptionStr = favor.getDescription();
    String favoCoinStr = "Worth " + (int) favor.getReward() + " coins";
    setupTextView(rootView, R.id.time, timeStr);
    setupTextView(rootView, R.id.title, titleStr);
    setupTextView(rootView, R.id.description, descriptionStr);
    setupTextView(rootView, R.id.value, favoCoinStr);
    setupImageView(rootView, favor);
    isRequested =
        favor.getUserIds().get(0).equals(DependencyFactory.getCurrentFirebaseUser().getUid());
    displayUserProfile(favor);
    // update status string
    favorStatus = verifyFavorHasBeenAccepted(favor);
    updateAppBarMenuDisplay();
    updateDisplayFromViewStatus();
  }

  void displayUserProfile(Favor favor) {
    if (isRequested) {
      // display user profile
      FirebaseUser user = DependencyFactory.getCurrentFirebaseUser();
      if (user.getPhotoUrl() != null) {
        Glide.with(this)
            .load(user.getPhotoUrl())
            .fitCenter()
            .into((ImageView) requireView().findViewById(R.id.user_profile_picture));
      }
      // display committed user list
      requireView().findViewById(R.id.commit_user_group).setVisibility(View.VISIBLE);
      if (favor.getUserIds().size() > 1) setupUserListView();

      ((TextView) requireView().findViewById(R.id.user_name)).setText(DependencyFactory.getCurrentFirebaseUser().getDisplayName());
    }
    else {
      UserUtil.getSingleInstance()
              .findUser(favor.getRequesterId())
              .thenAccept(
                      user -> {
                        String name = user.getName();
                        if (name == null || name.equals(""))
                          name = user.getEmail().split("@")[0].replace(".", " ");
                        ((TextView) requireView().findViewById(R.id.user_name)).setText(name);
                      });
    }
  }

  private void setupImageView(View rootView, Favor favor) {
    String url = favor.getPictureUrl();
    if (url != null) {
      ImageView imageView = rootView.findViewById(R.id.picture);
      View loadingPanelView = rootView.findViewById(R.id.loading_panel);

      loadingPanelView.setVisibility(View.VISIBLE);
      getViewModel()
          .downloadPicture(favor)
          .thenAccept(
              picture -> {
                imageView.setImageBitmap(picture);
                loadingPanelView.setVisibility(View.GONE);
              });
    } else rootView.findViewById(R.id.picture).setVisibility(View.GONE);
  }

  private void setupUserListView() {
    ListView listView = requireView().findViewById(R.id.commit_user);
    for (String userId : currentFavor.getUserIds()) {
      if (!userId.equals(currentFavor.getRequesterId()))
        UserUtil.getSingleInstance()
            .findUser(userId)
            .thenAccept(
                user -> {
                  commitUsers.put(userId, user);
                  listView.setAdapter(
                      new UserAdapter(getContext(), new ArrayList<>(commitUsers.values())));
                });
    }
    listView.setOnItemClickListener(
        (parent, view, position, id) -> {
          User user = (User) parent.getItemAtPosition(position);
          PopupMenu popup = new PopupMenu(requireActivity(), view);
          popup.getMenuInflater().inflate(R.menu.user_popup_menu, popup.getMenu());
          if (currentFavor.getStatusId() != FavorStatus.REQUESTED.toInt())
            popup.getMenu().findItem(R.id.accept).setVisible(false);
          popup.setOnMenuItemClickListener(
              item -> {
                if (item.getItemId() == R.id.accept) {
                  acceptFavor(user);
                } else if (item.getItemId() == R.id.profile) {
                  Bundle userBundle = new Bundle();
                  userBundle.putString(CommonTools.USER_ARGS, user.getId());
                  Navigation.findNavController(requireView())
                      .navigate(R.id.action_nav_favorPublishedView_to_UserInfoPage, userBundle);
                }
                return false;
              });
          popup.show();
        });
  }

  private void updateDisplayFromViewStatus() {
    toolbar.setTitle(favorStatus.toString());
    switch (favorStatus) {
      case SUCCESSFULLY_COMPLETED:
        {
          requireView().findViewById(R.id.buttons_bar).setVisibility(View.GONE);
          break;
        }
      case ACCEPTED:
        {
          updateCompleteBtnDisplay(
              R.string.complete_favor, true, R.drawable.ic_check_box_black_24dp);
          toolbar.setBackgroundColor(getResources().getColor(R.color.accepted_status_bg));
          break;
        }
      case REQUESTED:
        {
          if (currentFavor
              .getUserIds()
              .contains(DependencyFactory.getCurrentFirebaseUser().getUid()))
            updateCompleteBtnDisplay(R.string.accept_favor, false, R.drawable.ic_thumb_up_24dp);
          else updateCompleteBtnDisplay(R.string.accept_favor, true, R.drawable.ic_thumb_up_24dp);
          toolbar.setBackgroundColor(getResources().getColor(R.color.requested_status_bg));
          break;
        }
      case COMPLETED_ACCEPTER:
        {
          if (!isRequested)
            updateCompleteBtnDisplay(
                R.string.wait_complete, false, R.drawable.ic_watch_later_black_24dp);
          else
            updateCompleteBtnDisplay(
                R.string.complete_favor, true, R.drawable.ic_check_box_black_24dp);
          break;
        }
      case COMPLETED_REQUESTER:
        {
          if (isRequested)
            updateCompleteBtnDisplay(
                R.string.wait_complete, false, R.drawable.ic_watch_later_black_24dp);
          else
            updateCompleteBtnDisplay(
                R.string.complete_favor, true, R.drawable.ic_check_box_black_24dp);
          break;
        }
      default: // includes accepted by other
        requireView().findViewById(R.id.buttons_bar).setVisibility(View.GONE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.cancelled_status_bg));
    }
  }

  private void updateCompleteBtnDisplay(int txt, boolean visible, int icon) {
    acceptAndCompleteFavorBtn.setText(txt);
    acceptAndCompleteFavorBtn.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    acceptAndCompleteFavorBtn.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
  }

  private void enableButtons(boolean enable) {
    acceptAndCompleteFavorBtn.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    chatBtn.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
  }

  private void setupTextView(View rootView, int id, String text) {
    TextView textView = rootView.findViewById(id);
    textView.setText(text);
    textView.setKeyListener(null);
  }

  private void commitFavor() {
    currentFavor.setAccepterId(DependencyFactory.getCurrentFirebaseUser().getUid());
    CompletableFuture updateFuture = getViewModel().commitFavor(currentFavor, 1);
    updateFuture.thenAccept(
        o ->
            CommonTools.showSnackbar(requireView(), getString(R.string.favor_respond_success_msg)));
    updateFuture.exceptionally(onFailedResult(requireView()));
  }

  private void cancelCommit() {
    for (int i = 1; i < currentFavor.getUserIds().size(); i++) {
      if (currentFavor
          .getUserIds()
          .get(i)
          .equals(DependencyFactory.getCurrentFirebaseUser().getUid()))
        currentFavor.getUserIds().remove(i);
    }
    CompletableFuture updateFuture = getViewModel().commitFavor(currentFavor, -1);
    updateFuture.thenAccept(
        o ->
            CommonTools.showSnackbar(requireView(), getString(R.string.favor_respond_success_msg)));
    updateFuture.exceptionally(onFailedResult(requireView()));
  }

  private void acceptFavor(User user) {
    CompletableFuture acceptFavorFuture = getViewModel().acceptFavor(currentFavor, user);
    acceptFavorFuture.thenAccept(
        o -> {
          CommonTools.showSnackbar(getView(), getString(R.string.favor_respond_success_msg));
          UserUtil.getSingleInstance()
              .findUser(user.getId())
              .thenAccept(
                  user1 -> {
                    user1.setAcceptedFavors(user1.getAcceptedFavors() + 1);
                    UserUtil.getSingleInstance().updateUser(user1);
                  });
        });
    // acceptFavorFuture.exceptionally(handleException());
  }

  private void completeFavor() {
    CompletableFuture updateFuture = getViewModel().completeFavor(currentFavor, isRequested);
    updateFuture.thenAccept(
        o ->
            CommonTools.showSnackbar(
                requireView(), getString(R.string.favor_complete_success_msg)));
    updateFuture.exceptionally(onFailedResult(requireView()));
  }

  private void goEditFavor() {
    currentFavor.setStatusIdToInt(FavorStatus.EDIT);
    Bundle favorBundle = new Bundle();
    favorBundle.putParcelable(CommonTools.FAVOR_VALUE_ARGS, currentFavor);
    favorBundle.putString(
        CommonTools.FAVOR_SOURCE, getString(R.string.favor_source_publishedFavor));
    findNavController(requireActivity(), R.id.nav_host_fragment)
        .navigate(R.id.action_global_favorEditingView, favorBundle);
  }

  private void goRestartFavor() {
    // restart a favor with different favorId
    Favor newFavor =
        new Favor(
            "", "", DependencyFactory.getCurrentFirebaseUser().getUid(), null, FavorStatus.EDIT, 0);
    newFavor.updateToOther(currentFavor);
    Bundle favorBundle = new Bundle();
    favorBundle.putParcelable(CommonTools.FAVOR_VALUE_ARGS, newFavor);
    favorBundle.putString(
        CommonTools.FAVOR_SOURCE, getString(R.string.favor_source_publishedFavor));
    findNavController(requireActivity(), R.id.nav_host_fragment)
        .navigate(R.id.action_global_favorEditingView, favorBundle);
  }

  private void cancelFavor() {
    CompletableFuture completableFuture = getViewModel().cancelFavor(currentFavor, isRequested);
    completableFuture.thenAccept(successfullyCancelledConsumer());
    completableFuture.exceptionally(onFailedResult(requireView()));
  }

  private void deleteFavor() {
    CompletableFuture deleteFuture = getViewModel().deleteFavor(currentFavor);
    deleteFuture.thenAccept(
        o -> {
          CommonTools.showSnackbar(requireView(), getString(R.string.favor_delete_success_msg));
          requireActivity().onBackPressed();
        });
    deleteFuture.exceptionally(onFailedResult(requireView()));
  }

  private Consumer successfullyCancelledConsumer() {
    return o -> {
      CommonTools.showSnackbar(getView(), getString(R.string.favor_cancel_success_msg));
    };
  }

  // Verifies favor hasn't already been accepted
  private FavorStatus verifyFavorHasBeenAccepted(Favor favor) {
    FavorStatus favorStatus = FavorStatus.toEnum(favor.getStatusId());
    if (favor.getAccepterId() != null
        && !favor.getRequesterId().equals(DependencyFactory.getCurrentFirebaseUser().getUid())) {
      if (favorStatus.equals(FavorStatus.ACCEPTED)
          && !favor.getAccepterId().equals(DependencyFactory.getCurrentFirebaseUser().getUid())) {
        favorStatus = FavorStatus.ACCEPTED_BY_OTHER;
      }
    }
    return favorStatus;
  }

  private Function onFailedResult(View currentView) {
    return (exception) -> {
      if (((CompletionException) exception).getCause() instanceof IllegalRequestException)
        CommonTools.showSnackbar(currentView, getString(R.string.illegal_request_error));
      else CommonTools.showSnackbar(currentView, getString(R.string.update_favor_error));
      Log.e(TAG, Objects.requireNonNull(((Exception) exception).getMessage()));
      return null;
    };
  }
}
