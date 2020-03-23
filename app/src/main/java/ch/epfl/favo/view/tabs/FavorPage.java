package ch.epfl.favo.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.view.ViewController;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;
import ch.epfl.favo.view.tabs.favorList.FavorAdapter;

/**
 * View will contain list of favors requested in the past. The list will contain clickable items
 * that will expand to give more information about them. This object is a simple {@link Fragment}
 * subclass.
 */
public class FavorPage extends Fragment implements View.OnClickListener {

  private ArrayList<Favor> activeFavorArrayList;
  private ArrayList<Favor> pastFavorArrayList;

  public FavorPage() {
    // Required empty public constructor
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (savedInstanceState != null) {
      activeFavorArrayList = savedInstanceState.getParcelableArrayList("activeFavorArrayList");
      pastFavorArrayList = savedInstanceState.getParcelableArrayList("pastFavorArrayList");
    } else {
      activeFavorArrayList = new ArrayList<>();
      pastFavorArrayList = new ArrayList<>();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList("activeFavorArrayList", activeFavorArrayList);
    outState.putParcelableArrayList("pastFavorArrayList", pastFavorArrayList);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setupView();

    View rootView = inflater.inflate(R.layout.fragment_favorpage, container, false);
    rootView.findViewById(R.id.new_favor).setOnClickListener(this);

    // Todo: Cache results from getActiveFavorList() and getPastFavorList().
    Spinner spinner = rootView.findViewById(R.id.spinner);
    ListView listView = rootView.findViewById(R.id.favor_list);
    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
              case 0: // default: active favors
                if (activeFavorArrayList.size() == 0) {
                  TextView tipTextView = rootView.findViewById(R.id.tip);
                  tipTextView.setText("You have no active favor  （・∀・）");
                  tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                  tipTextView.setVisibility(View.VISIBLE);
                } else {
                  listView.setAdapter(new FavorAdapter(getContext(), activeFavorArrayList));
                }
                break;
              case 1: // past favors
                if (pastFavorArrayList.size() == 0) {
                  TextView tipTextView = rootView.findViewById(R.id.tip);
                  tipTextView.setText("You have no past favor   （・∀・）");
                  tipTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                  tipTextView.setVisibility(View.VISIBLE);
                } else {
                  listView.setAdapter(new FavorAdapter(getContext(), pastFavorArrayList));
                }
                break;
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });

    return rootView;
  }


  private ArrayList<Favor> genFavor(String s, int n) {
    ArrayList<Favor> favorList = new ArrayList<>();
    for (int i = 0; i < n; ++i) {
      favorList.add(new Favor(String.format("%s%d", s, i), "desc", null, null, 0));
    }
    return favorList;
  }

  @Override
  public void onClick(View view) {
    Fragment fragment;

    // The following inspection warning is suppressed. More cases will be added soon.
    // noinspection SwitchStatementWithTooFewBranches
    switch (view.getId()) {
      case R.id.new_favor:
        fragment = new FavorRequestView();
        replaceFragment(fragment);
        break;
    }
  }

  private void setupView() {
    ((ViewController) getActivity()).setupViewTopDestTab();
    ((ViewController) getActivity()).checkFavListViewButton();
  }

  // Replace the current fragment with the new fragment.
  // Todo: Seems useful. Try to put this method in a util package and import it here.
  private void replaceFragment(Fragment newFragment) {
    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
    transaction.replace(R.id.nav_host_fragment, newFragment);
    transaction.addToBackStack(null);
    transaction.commit();
    // transaction.remove(this);
  }
}
