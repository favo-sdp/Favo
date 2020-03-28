package ch.epfl.favo.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;

public class FavorDetailPage extends Fragment {

  private View rootView;
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
    title = args.getString("title", "defaultTitle");
    location = args.getString("location", "defaultLocation");
    datetime = args.getString("datetime", "defaultDatetime");
    description = args.getString("description", "defautlDescription");
  }

  @Override
  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    rootView = inflater.inflate(R.layout.fragment_favor_details, container, false);

    initTextView(R.id.favor_details_title, title);
    initTextView(R.id.favor_details_datetime, datetime);
    initTextView(R.id.favor_details_location, location);
    initTextView(R.id.favor_details_details, description);

    return rootView;
  }

  private void initTextView(int textViewId, String string) {
    TextView view = rootView.findViewById(textViewId);
    view.setText(string);
  }
}
