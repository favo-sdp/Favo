package ch.epfl.favo.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.view.ViewController;

public class FavorDetailPage extends Fragment {

  private View rootView;

  private String TITLE = "Title";
  private String LOCATION = "Loc";
  private String DATETIME = "Time";
  private String DESCRIPTION = "Desc";

  private String title;
  private String location;
  private String datetime;
  private String description;

  public FavorDetailPage() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    Bundle args = this.getArguments();
    assert args != null;
    title = args.getString(TITLE, "defaultTitle");
    location = args.getString(LOCATION, "defaultLocation");
    datetime = args.getString(DATETIME, "defaultDatetime");
    description = args.getString(DESCRIPTION, "defaultDescription");
  }

  @Override
  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    setupView();
    rootView = inflater.inflate(R.layout.fragment_favor_details, container, false);

    initTextView(R.id.favor_details_title, TITLE, title);
    initTextView(R.id.favor_details_datetime, DATETIME, datetime);
    initTextView(R.id.favor_details_location, LOCATION, location);
    initTextView(R.id.favor_details_details, DESCRIPTION, description);

    Button button = rootView.findViewById(R.id.favor_details_button);
    button.setOnClickListener(v -> {
      Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
    });

    return rootView;
  }

  private void initTextView(int textViewId, String key, String value) {
    TextView view = rootView.findViewById(textViewId);
    view.setText(String.format("%s: %s", key, value));
  }

  private void setupView(){
    ((ViewController) Objects.requireNonNull(getActivity())).hideBottomTabs();
    ((ViewController) Objects.requireNonNull(getActivity())).showBackIcon();
  }
}
