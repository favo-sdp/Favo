package ch.epfl.favo.view.tabs.addFavor;

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

import org.jetbrains.annotations.NotNull;

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
  private Button confirmFavorBtn;
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
    confirmFavorBtn = rootView.findViewById(R.id.accept_button);
    locationAccessBtn = rootView.findViewById(R.id.location_accept_view_btn);
    chatBtn = rootView.findViewById(R.id.chat_button_accept_view);

    // Show snackbar when favor has been confirmed
    confirmFavorBtn.setOnClickListener(v -> acceptFavor());
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @NotNull
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
          if (((CompletionException) e).getCause().equals(FavorAlreadyAcceptedException.class)) {
            CommonTools.showSnackbar(getView(), "Favor has already been accepted :(");
            favor.setStatusId(Favor.Status.ACCEPTED_BY_OTHER);
            updateStatusDisplayFromFavorStatus();
          }
          CommonTools.showSnackbar(getView(), "Failed to update");
          return null;
        });
  }

  private void displayFromFavor(View rootView, Favor favor) {
    //    String greetingStr = "favor of " + favor.getRequesterId();
    //    String locationStr =
    //        "latitude: "
    //            + String.format("%.4f", favor.getLocation().getLatitude())
    //            + " longitude: "
    //            + String.format("%.4f", favor.getLocation().getLongitude());
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
    switch (newStatus) {
      case SUCCESSFULLY_COMPLETED:
        {
        }
      case ACCEPTED:
        {
          enableButtons(false, true);
          statusText.setBackgroundColor(getResources().getColor(R.color.accepted_status_bg));
          break;
        }

      case REQUESTED:
        {
          enableButtons(true, true);
          statusText.setBackgroundColor(getResources().getColor(R.color.requested_status_bg));
          break;
        }
      case ACCEPTED_BY_OTHER:
        {
          enableButtons(false, false);
        }
      default: // includes accepted by other
        enableButtons(false, false);
        statusText.setBackgroundColor(getResources().getColor(R.color.cancelled_status_bg));
    }
  }

  private void enableButtons(boolean acceptButton, boolean chatButton) {
    confirmFavorBtn.setEnabled(acceptButton);
    chatBtn.setEnabled(chatButton);
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
