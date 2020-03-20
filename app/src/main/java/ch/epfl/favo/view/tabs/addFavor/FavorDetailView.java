package ch.epfl.favo.view.tabs.addFavor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.view.ViewController;

public class FavorDetailView extends Fragment {
  private static final String FAVOR_ARGS = "FAVOR_ARGS";
  private Favor favor;

  public static FavorDetailView newInstance(Favor favor) {
    FavorDetailView fragment = new FavorDetailView();
    Bundle args = new Bundle();
    args.putParcelable(FAVOR_ARGS, favor);
    fragment.setArguments(args);
    return fragment;
  }

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

    // Show snackbar when favor has been confirmed
    confirmFavorBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            showSnackbar(getString(R.string.favor_respond_success_msg));
          }
        });

    if (favor == null) {
      favor = getArguments().getParcelable(FAVOR_ARGS);
    }
    displayFromFavor(rootView, favor);

    return rootView;
  }

  public void displayFromFavor(View rootView, Favor favor) {
    String greetingStr = "favor of " + favor.getRequesterId();
    String locationStr =
        "latitude: "
            + String.format("%.4f", favor.getLocation().getLatitude())
            + " longitude: "
            + String.format("%.4f", favor.getLocation().getLongitude());
    String timeStr = CommonTools.convertTime(favor.getLocation().getTime());
    String titleStr = favor.getTitle();
    String descriptionStr = favor.getDescription();

    TextView greeting = rootView.findViewById(R.id.favor_greeting_text_accept);
    greeting.setText(greetingStr);

    TextView location = rootView.findViewById(R.id.location_accept_view);
    location.setText(locationStr);

    TextView time = rootView.findViewById(R.id.datetime_accept_view);
    time.setText(timeStr);

    TextView title = rootView.findViewById(R.id.title_accept_view);
    title.setText(titleStr);

    TextView details = rootView.findViewById(R.id.details_accept_view);
    details.setText(descriptionStr);
  }

  private void setupView() {
    ((ViewController) getActivity()).setupViewBotDestTab();
  }

  private void showSnackbar(String errorMessageRes) {
    Snackbar.make(
            requireView().findViewById(R.id.fragment_favor_accept_view),
            errorMessageRes,
            Snackbar.LENGTH_LONG)
        .show();
  }
}
