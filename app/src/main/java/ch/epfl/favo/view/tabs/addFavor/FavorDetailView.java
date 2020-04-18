package ch.epfl.favo.view.tabs.addFavor;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.FavorFragmentFactory;

@SuppressLint("NewApi")
public class FavorDetailView extends Fragment {

  private FavorViewStatus viewStatus;
  private Favor currentFavor;
  private FloatingActionButton locationAccessBtn;
  private Button acceptAndCancelFavorBtn;
  private Button chatBtn;
  private TextView statusText;

  public FavorDetailView() {
    // create favor detail from a favor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // inflate view
    View rootView = inflater.inflate(R.layout.fragment_favor_accept_view, container, false);

    ((MainActivity) requireActivity()).hideBottomNavigation();

    setupButtons(rootView);
    statusText = rootView.findViewById(R.id.status_text_accept_view);

    if (currentFavor == null) {
      currentFavor = getArguments().getParcelable(FavorFragmentFactory.FAVOR_ARGS);
    }
    displayFromFavor(rootView, currentFavor);

    return rootView;
  }

  private void setupButtons(View rootView) {
    acceptAndCancelFavorBtn = rootView.findViewById(R.id.accept_button);
    locationAccessBtn = rootView.findViewById(R.id.location_accept_view_btn);
    chatBtn = rootView.findViewById(R.id.chat_button_accept_view);

    // If clicking for the first time, then accept the favor
    acceptAndCancelFavorBtn.setOnClickListener(
        v -> {
          if (currentFavor.getStatusId() == Favor.Status.REQUESTED) {
            acceptFavor();
          } else {
            cancelFavor();
          }
        });

    chatBtn.setOnClickListener(
        v -> {
          Bundle favorBundle = new Bundle();
          favorBundle.putParcelable("FAVOR_ARGS", currentFavor);
          Navigation.findNavController(requireView())
              .navigate(R.id.action_nav_favorDetailView_to_chatView, favorBundle);
        });
  }

  private void cancelFavor() {
    CompletableFuture completableFuture =
        FavorUtil.getSingleInstance()
            .updateFavorStatus(currentFavor.getId(), Favor.Status.CANCELLED_ACCEPTER);
    completableFuture.thenAccept(successfullyCancelledConsumer());
    completableFuture.exceptionally(favorFailedToBeAcceptedConsumer());
  }

  private Consumer successfullyCancelledConsumer() {
    return o -> {
      CommonTools.showSnackbar(getView(), getString(R.string.favor_cancel_success_msg));

      // update UI
      currentFavor.setStatusId(Favor.Status.CANCELLED_ACCEPTER);
      viewStatus = convertFavorStatusToViewStatus(currentFavor);
      updateDisplayFromViewStatus();
    };
  }

  private FavorViewStatus convertFavorStatusToViewStatus(Favor favor) {
    // Equivalent mapping except for the accepted case. Here we check if the
    // accepter Id matches the current user
    Favor.Status favorStatus = favor.getStatusId();
    FavorViewStatus resultStatus;
    if (favorStatus.equals(Favor.Status.ACCEPTED)
        && !favor.getRequesterId().equals(UserUtil.currentUserId)) {
      resultStatus = FavorViewStatus.ACCEPTED_BY_OTHER;
    } else { // need to ensure that the name is the same as the name in Favor class
      resultStatus = FavorViewStatus.valueOf(favorStatus.toString());
    }
    return resultStatus;
  }

  private void acceptFavor() {
    CompletableFuture<Favor> favorFuture =
        FavorUtil.getSingleInstance().retrieveFavor(currentFavor.getId());
    favorFuture // get updated favor from db
        .thenAccept(
        favor -> {
          if (!favor.contentEquals(currentFavor)) { // if favor changed, update the view
            currentFavor.updateToOther(favor);
            CommonTools.showSnackbar(getView(), getString(R.string.favor_remotely_changed_msg));
            displayFromFavor(getView(), favor);
          } else { // update DB with accepted status
            CompletableFuture updateFavorFuture =
                FavorUtil.getSingleInstance()
                    .updateFavorStatus(favor.getId(), Favor.Status.ACCEPTED);
            updateFavorFuture.thenAccept(favorAcceptedConsumer());
            updateFavorFuture.exceptionally(favorFailedToBeAcceptedConsumer());
          }
        });
    favorFuture.exceptionally(favorFailedToBeAcceptedConsumer());
  }

  private Function favorFailedToBeAcceptedConsumer() {
    return e -> {
      // if already accepted then change the status display and disable all the buttons
      CommonTools.showSnackbar(getView(), getString(R.string.update_favor_error));
      return null;
    };
  }

  private Consumer favorAcceptedConsumer() {
    return o -> {
      CommonTools.showSnackbar(getView(), getString(R.string.favor_respond_success_msg));

      currentFavor.setStatusId(Favor.Status.ACCEPTED);
      viewStatus = FavorViewStatus.ACCEPTED;
      updateDisplayFromViewStatus();
    };
  }

  private void displayFromFavor(View rootView, Favor favor) {

    String timeStr = CommonTools.convertTime(favor.getLocation().getTime());
    String titleStr = favor.getTitle();
    String descriptionStr = favor.getDescription();
    // update status string
    viewStatus = convertFavorStatusToViewStatus(favor);
    updateDisplayFromViewStatus();

    setupTextView(rootView, R.id.datetime_accept_view, timeStr);
    setupTextView(rootView, R.id.title_accept_view, titleStr);
    setupTextView(rootView, R.id.details_accept_view, descriptionStr);
  }

  private void updateDisplayFromViewStatus() {
    statusText.setText(viewStatus.getPrettyString());
    updateButtonDisplay();
    switch (viewStatus) {
      case SUCCESSFULLY_COMPLETED:
        {
          enableButtons(false);
        }
      case ACCEPTED:
        {
          statusText.setBackgroundColor(getResources().getColor(R.color.accepted_status_bg));
          break;
        }

      case REQUESTED:
        {
          statusText.setBackgroundColor(getResources().getColor(R.color.requested_status_bg));
          break;
        }
      default: // includes accepted by other
        enableButtons(false);
        statusText.setBackgroundColor(getResources().getColor(R.color.cancelled_status_bg));
    }
  }

  private void updateButtonDisplay() {
    String displayMessage;
    int backgroundColor;
    Drawable img;
    if (viewStatus == FavorViewStatus.ACCEPTED) {
      displayMessage = getString(R.string.cancel_accept_button_display);
      backgroundColor = R.color.fui_transparent;
      img = getResources().getDrawable(R.drawable.ic_cancel_24dp);
    } else { // includes ACCEPTED_BY_OTHER
      displayMessage = getResources().getString(R.string.accept_favor);
      img = getResources().getDrawable(R.drawable.ic_thumb_up_24dp);
      backgroundColor = android.R.drawable.btn_default;
    }
    acceptAndCancelFavorBtn.setText(displayMessage);
    acceptAndCancelFavorBtn.setBackgroundResource(backgroundColor);
    acceptAndCancelFavorBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
  }

  private void enableButtons(boolean enable) {
    acceptAndCancelFavorBtn.setEnabled(enable);
    chatBtn.setEnabled(enable);
  }

  private void setupTextView(View rootView, int id, String text) {
    TextView textView = rootView.findViewById(id);
    textView.setText(text);
    textView.setKeyListener(null);
  }
}
