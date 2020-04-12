package ch.epfl.favo.view.tabs.addFavor;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.common.FavorAlreadyAcceptedException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.FavorFragmentFactory;
import ch.epfl.favo.view.ViewController;

public class FavorDetailView extends Fragment {
  private Favor favor;
  private FloatingActionButton locationAccessBtn;
  private Button acceptAndCancelFavorBtn;
  private Button chatBtn;
  private TextView statusText;

  public FavorDetailView() {
    // create favor detail from a favor
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // hide bottom tabs and hamburger menu
    setupView();
    // inflate view
    View rootView = inflater.inflate(R.layout.fragment_favor_accept_view, container, false);
    setupButtons(rootView);
    statusText = rootView.findViewById(R.id.status_text_accept_view);

    if (favor == null) {
      favor = getArguments().getParcelable(FavorFragmentFactory.FAVOR_ARGS);
    }
    displayFromFavor(rootView, favor);

    return rootView;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void setupButtons(View rootView) {
    acceptAndCancelFavorBtn = rootView.findViewById(R.id.accept_button);
    locationAccessBtn = rootView.findViewById(R.id.location_accept_view_btn);
    chatBtn = rootView.findViewById(R.id.chat_button_accept_view);

    // If clicking for the first time, then accept the favor
    acceptAndCancelFavorBtn.setOnClickListener(
        v -> {
          if (favor.getStatusId() == Favor.Status.REQUESTED) {
            acceptFavor();
          } else {
            cancelFavor();
          }
        });
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void cancelFavor() {
    CompletableFuture completableFuture =
        FavorUtil.getSingleInstance()
            .updateFavorStatus(favor.getId(), Favor.Status.CANCELLED_ACCEPTER);
    completableFuture.thenAccept(
            o -> {
              CommonTools.showSnackbar(getView(), getString(R.string.favor_cancel_success_msg));
            });
    completableFuture.exceptionally(
            e -> {
              CommonTools.showSnackbar(getView(), getString(R.string.update_favor_error));
              return null;
            });
    //update UI
    favor.setStatusId(Favor.Status.CANCELLED_ACCEPTER);
    ((MainActivity) getActivity()).activeFavors.remove(favor.getId());
    updateStatusDisplayFromFavorStatus();
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void acceptFavor() {
    CompletableFuture acceptFavorFuture = FavorUtil.getSingleInstance().acceptFavor(favor.getId());
    acceptFavorFuture.thenAccept(
        o -> {
          CommonTools.showSnackbar(getView(), getString(R.string.favor_respond_success_msg));
          ((MainActivity) requireActivity()).activeFavors.put(favor.getId(), favor);
          favor.setStatusId(Favor.Status.ACCEPTED);
          updateStatusDisplayFromFavorStatus();
        });
    acceptFavorFuture.exceptionally(
        e -> {
          // if already accepted then change the status display and disable all the buttons
          if (((CompletionException) e)
              .getCause()
              .getClass()
              .equals(FavorAlreadyAcceptedException.class)) {
            CommonTools.showSnackbar(getView(), "Favor is no longer available");
            favor.setStatusId(Favor.Status.ACCEPTED_BY_OTHER);
            updateStatusDisplayFromFavorStatus();
          }
          // if any other error, then just print a simple message
          else {
            CommonTools.showSnackbar(getView(), getString(R.string.update_favor_error));
          }
          return null;
        });
  }

  private void displayFromFavor(View rootView, Favor favor) {

    String timeStr = CommonTools.convertTime(favor.getLocation().getTime());
    String titleStr = favor.getTitle();
    String descriptionStr = favor.getDescription();
    // update status string
    updateStatusDisplayFromFavorStatus();

    setupTextView(rootView, R.id.datetime_accept_view, timeStr);
    setupTextView(rootView, R.id.title_accept_view, titleStr);
    setupTextView(rootView, R.id.details_accept_view, descriptionStr);
  }

  private void updateStatusDisplayFromFavorStatus() {
    Favor.Status newStatus = favor.getStatusId();
    statusText.setText(newStatus.getPrettyString());
    updateButtonDisplay(newStatus);
    switch (newStatus) {
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

  private void updateButtonDisplay(Favor.Status status) {
    String displayMessage;
    int backgroundColor;
    Drawable img;
    if (status == Favor.Status.ACCEPTED) {
      displayMessage = "Cancel";
      backgroundColor = R.color.fui_transparent;
      img = getResources().getDrawable(R.drawable.ic_cancel_24dp);
    } else {
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

  private void setupView() {
    ((ViewController) getActivity()).setupViewBotDestTab();
  }
}
