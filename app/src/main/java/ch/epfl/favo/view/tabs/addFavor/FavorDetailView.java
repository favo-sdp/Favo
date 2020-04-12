package ch.epfl.favo.view.tabs.addFavor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.FavorFragmentFactory;
import ch.epfl.favo.view.ViewController;

public class FavorDetailView extends Fragment {
  private Favor favor;

  public FavorDetailView() {
    // create favor detail from a favor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // hide bottom tabs and hamburger menu
    setupView();
    // inflate view
    View rootView = inflater.inflate(R.layout.fragment_favor_accept_view, container, false);
    Button confirmFavorBtn = rootView.findViewById(R.id.accept_button);

    if (favor == null) {
      favor = getArguments().getParcelable(FavorFragmentFactory.FAVOR_ARGS);
    }
    displayFromFavor(rootView, favor);

    // Show snackbar when favor has been confirmed
    confirmFavorBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            CommonTools.showSnackbar(getView(), getString(R.string.favor_respond_success_msg));

            // Update accepterId in DB
            String accepterId = UserUtil.currentUserId;
            Map<String, Object> acceptedFavor = new HashMap<>();
            acceptedFavor.put("accepterId", accepterId);
            FavorUtil.getSingleInstance().updateFavor(favor.getId(), acceptedFavor);
          }
        });

    return rootView;
  }

  private void displayFromFavor(View rootView, Favor favor) {
    String greetingStr = "favor of " + favor.getRequesterId();
    String locationStr =
        "latitude: "
            + String.format("%.4f", favor.getLocation().getLatitude())
            + " longitude: "
            + String.format("%.4f", favor.getLocation().getLongitude());
    String timeStr = CommonTools.convertTime(favor.getLocation().getTime());
    String titleStr = favor.getTitle();
    String descriptionStr = favor.getDescription();

    setupTextView(rootView, R.id.favor_greeting_text_accept, greetingStr);
    setupTextView(rootView, R.id.location_accept_view, locationStr);
    setupTextView(rootView, R.id.datetime_accept_view, timeStr);
    setupTextView(rootView, R.id.title_accept_view, titleStr);
    setupTextView(rootView, R.id.details_accept_view, descriptionStr);
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
