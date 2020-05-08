package ch.epfl.favo.view.tabs.addFavor;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.epfl.favo.R;
import ch.epfl.favo.exception.IllegalRequestException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
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
  private boolean isRequested;

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
    inflater.inflate(R.menu.menu_favor_detail, menu);
    cancelItem = menu.findItem(R.id.cancel_button);
    editItem = menu.findItem(R.id.edit_button);
    if (favorStatus != null) {
      updateAcceptBtnDisplay();
    }
    if (isRequested) {
      editItem.setVisible(true);
    }
    super.onCreateOptionsMenu(menu, inflater);
  }

  // handle button activities
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.cancel_button) {
      cancelFavor();
    } else if (id == R.id.edit_button){
      cancelFavor();
      currentFavor.setStatusIdToInt(FavorStatus.CANCELLED_REQUESTER);
      favorViewModel.setFavorValue(currentFavor);
      Bundle favorBundle = new Bundle();
      favorBundle.putString(CommonTools.FAVOR_ARGS, currentFavor.getId());
      findNavController(requireActivity(), R.id.nav_host_fragment)
              .navigate(R.id.action_global_favorEditingView, favorBundle);
    } else if (id == R.id.share_button){

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
    Log.d(TAG, (currentFavor==null)  + " ");
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
                  Log.d(TAG, favor.getStatusId() + " once");
                  displayFromFavor(rootView, currentFavor);
                }
              } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                CommonTools.showSnackbar(rootView, getString(R.string.error_database_sync));
                enableButtons(false);
              }
            });
  }

  private void setupButtons(View rootView) {
    acceptAndCompleteFavorBtn = rootView.findViewById(R.id.accept_button);
    TextView locationAccessBtn = rootView.findViewById(R.id.location);
    chatBtn = rootView.findViewById(R.id.chat_button);

    locationAccessBtn.setOnClickListener(
        v -> {
          favorViewModel.setShowObservedFavor(true);
          findNavController(requireActivity(), R.id.nav_host_fragment)
              .popBackStack(R.id.nav_map, false);
        });

    // If clicking for the first time, then accept the favor
    acceptAndCompleteFavorBtn.setOnClickListener(
        v -> {
          if (currentFavor.getStatusId() == FavorStatus.REQUESTED.toInt()) acceptFavor();
          else completeFavor();
        });

    chatBtn.setOnClickListener(
        v -> {
          Bundle favorBundle = new Bundle();
          favorBundle.putParcelable("FAVOR_ARGS", currentFavor);
          Navigation.findNavController(requireView())
              .navigate(R.id.action_nav_favorPublishedView_to_chatView, favorBundle);
        });

    rootView
        .findViewById(R.id.user_profile_picture)
        .setOnClickListener(
            v -> {
              Bundle userBundle = new Bundle();
              userBundle.putString("USER_ARGS", currentFavor.getRequesterId());
              Navigation.findNavController(requireView())
                  .navigate(R.id.action_nav_favorPublishedView_to_UserInfoPage, userBundle);
            });
  }

  private void completeFavor() {
    CompletableFuture updateFuture = getViewModel().completeFavor(currentFavor, isRequested);
    updateFuture.thenAccept(
        o ->
            CommonTools.showSnackbar(
                requireView(), getString(R.string.favor_complete_success_msg)));
    updateFuture.exceptionally(handleException());
  }

  private void cancelFavor() {
    CompletableFuture completableFuture = getViewModel().cancelFavor(currentFavor, isRequested);
    completableFuture.thenAccept(successfullyCancelledConsumer());
    completableFuture.exceptionally(handleException());
  }

  private Consumer successfullyCancelledConsumer() {
    return o -> {
      CommonTools.showSnackbar(getView(), getString(R.string.favor_cancel_success_msg));
    };
  }

  // Verifies favor hasn't already been accepted
  private FavorStatus verifyFavorHasBeenAccepted(Favor favor) {
    FavorStatus favorStatus = FavorStatus.toEnum(favor.getStatusId());
    if (favor.getAccepterId() != null) {
      if (favorStatus.equals(FavorStatus.ACCEPTED)
          && !favor.getAccepterId().equals(DependencyFactory.getCurrentFirebaseUser().getUid())) {
        favorStatus = FavorStatus.ACCEPTED_BY_OTHER;
      }
    }
    return favorStatus;
  }

  private void acceptFavor() {
    // TODO: we should prevent accepting favor by oneself, but with different way
    // this rare case only happens if I click the notification when someone accepts my favor, app
    // will open the detail view
    // of my requested favor, then I can accept my own favor.
    /* if (currentFavor.getRequesterId().equals(DependencyFactory.getCurrentFirebaseUser().getUid())) {
      CommonTools.showSnackbar(getView(), getString(R.string.favor_accept_by_oneself));
      return;
    }*/
    // update DB with accepted status
    CompletableFuture acceptFavorFuture = getViewModel().acceptFavor(currentFavor);
    acceptFavorFuture.thenAccept(
        o -> CommonTools.showSnackbar(getView(), getString(R.string.favor_respond_success_msg)));
    acceptFavorFuture.exceptionally(handleException());
  }

  private Function handleException() {
    return e -> {
      if (((CompletionException) e).getCause() instanceof IllegalRequestException)
        CommonTools.showSnackbar(requireView(), getString(R.string.illegal_accept_error));
      else CommonTools.showSnackbar(requireView(), getString(R.string.update_favor_error));
      return null;
    };
  }

  private void displayFromFavor(View rootView, Favor favor) {

    String timeStr = CommonTools.convertTime(favor.getLocation().getTime());
    String titleStr = favor.getTitle();
    String descriptionStr = favor.getDescription();
    // update status string
    favorStatus = verifyFavorHasBeenAccepted(favor);
    updateDisplayFromViewStatus();

    setupTextView(rootView, R.id.time, timeStr);
    setupTextView(rootView, R.id.title, titleStr);
    setupTextView(rootView, R.id.description, descriptionStr);

    UserUtil.getSingleInstance()
        .findUser(favor.getRequesterId())
        .thenAccept(
            user -> ((TextView) rootView.findViewById(R.id.user_name)).setText(user.getName()));
    Log.d(TAG, "once display");
    isRequested = favor.getUserIds().get(0).equals(DependencyFactory.getCurrentFirebaseUser().getUid());
    if (isRequested) {
      if (editItem != null) editItem.setVisible(true);
      FirebaseUser user = DependencyFactory.getCurrentFirebaseUser();
      if (user.getPhotoUrl() != null)
        Glide.with(this)
            .load(user.getPhotoUrl())
            .fitCenter()
            .into((ImageView) requireView().findViewById(R.id.user_profile_picture));
    }
  }

  private void updateDisplayFromViewStatus() {
    updateAcceptBtnDisplay();
    toolbar.setTitle(favorStatus.toString());
    switch (favorStatus) {
      case SUCCESSFULLY_COMPLETED:
        {
          updateCompleteBtnDisplay(
              R.string.complete_favor, false, R.drawable.ic_check_box_black_24dp);
          enableButtons(false);
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
          if (isRequested)
            updateCompleteBtnDisplay(
                R.string.wait_complete, false, R.drawable.ic_watch_later_black_24dp);
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
        enableButtons(false);
        updateCompleteBtnDisplay(R.string.wait_complete, false, R.drawable.ic_check_box_black_24dp);
        toolbar.setBackgroundColor(getResources().getColor(R.color.cancelled_status_bg));
    }
  }

  private void updateCompleteBtnDisplay(int txt, boolean clickable, int icon) {
    acceptAndCompleteFavorBtn.setText(txt);
    acceptAndCompleteFavorBtn.setClickable(clickable);
    // completeBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0);
  }

  private void updateAcceptBtnDisplay() {
    // String displayMessage;
    // int backgroundColor;
    boolean visible;
    // Drawable img;
    // displayMessage = getString(R.string.complete_favor);
    // backgroundColor = R.color.fui_transparent;
    // img = getResources().getDrawable(R.drawable.ic_check_box_black_24dp);
    // includes ACCEPTED_BY_OTHER
    // displayMessage = getString(R.string.accept_favor);
    // img = getResources().getDrawable(R.drawable.ic_thumb_up_24dp);
    // backgroundColor = android.R.drawable.btn_default;
    visible =
        favorStatus == FavorStatus.ACCEPTED
            || favorStatus == FavorStatus.COMPLETED_ACCEPTER
            || favorStatus == FavorStatus.COMPLETED_REQUESTER;
    if (cancelItem != null) cancelItem.setVisible(visible);
    // acceptAndCompleteFavorBtn.setText(displayMessage);
    // acceptAndCompleteFavorBtn.setBackgroundResource(backgroundColor);
    // acceptAndCancelFavorBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
  }

  private void enableButtons(boolean enable) {
    acceptAndCompleteFavorBtn.setEnabled(enable);
    chatBtn.setEnabled(enable);
  }

  private void setupTextView(View rootView, int id, String text) {
    TextView textView = rootView.findViewById(id);
    textView.setText(text);
    textView.setKeyListener(null);
  }
}
