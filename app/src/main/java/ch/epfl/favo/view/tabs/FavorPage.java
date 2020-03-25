package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.view.ViewController;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;
import ch.epfl.favo.view.tabs.favorList.FavorAdapter;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */
public class FavorPage extends Fragment implements View.OnClickListener {

  private ArrayList<Favor> activeFavors;
  private ArrayList<Favor> archivedFavors;
  private TextView tipTextView;

  public FavorPage() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    // Extract two arrayLists from the main activity
    MainActivity activity = (MainActivity) Objects.requireNonNull(getActivity());
    activeFavors = activity.getActiveFavorArrayList();
    archivedFavors = activity.getarchivedFavorArrayList();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setupView();

    View rootView = inflater.inflate(R.layout.fragment_favorpage, container, false);
    rootView.findViewById(R.id.new_favor).setOnClickListener(this);

    tipTextView = rootView.findViewById(R.id.tip);
    tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

    Spinner spinner = rootView.findViewById(R.id.spinner);
    ListView listView = rootView.findViewById(R.id.favor_list);

    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
              case 0: // default: active favors
                if (activeFavors.isEmpty()) showText(getString(R.string.favor_no_active_favor));
                else tipTextView.setVisibility(View.INVISIBLE);
                listView.setAdapter(new FavorAdapter(getContext(), activeFavors));
                break;
              case 1: // past favors
                if (archivedFavors.isEmpty()) showText(getString(R.string.favor_no_archived_favor));
                else tipTextView.setVisibility(View.INVISIBLE);
                listView.setAdapter(new FavorAdapter(getContext(), archivedFavors));
                break;
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });

    return rootView;
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.new_favor:
        CommonTools.replaceFragment(
                R.id.nav_host_fragment, getParentFragmentManager(), new FavorRequestView());
        break;
    }
  }

  private void showText(String text) {
    tipTextView.setText(text);
    tipTextView.setVisibility(View.VISIBLE);
  }

  private void setupView() {
    ((ViewController) Objects.requireNonNull(getActivity())).setupViewTopDestTab();
    ((ViewController) Objects.requireNonNull(getActivity())).checkFavListViewButton();
  }
}
