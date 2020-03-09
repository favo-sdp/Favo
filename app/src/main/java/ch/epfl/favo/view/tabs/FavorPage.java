package ch.epfl.favo.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;
import ch.epfl.favo.view.tabs.favorList.FavorAdapter;

/**
 * View will contain list of favors requested in the past.
 * The list will contain clickable items that will expand
 * to give more information about them.
 * This object is a simple {@link Fragment} subclass.
 */
public class FavorPage extends Fragment implements View.OnClickListener {

    public FavorPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorpage, container, false);

        // onClickListener for new_favor button.
        rootView.findViewById(R.id.new_favor).setOnClickListener(this);

        // onItemSelectedListener for spinner and listAdapter for listView.
        // Todo: Cache results from getActiveFavorList() and getPastFavorList().
        Spinner spinner = rootView.findViewById(R.id.spinner);
        ListView listView = rootView.findViewById(R.id.favor_list);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // default: active favors
                        listView.setAdapter(new FavorAdapter(getContext(), getActiveFavorList()));
                        break;
                    case 1: // past favors
                        listView.setAdapter(new FavorAdapter(getContext(), getPastFavorList()));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return rootView;
    }

    private ArrayList<Favor> getPastFavorList() {
        ArrayList<Favor> favorList = new ArrayList<>();
        favorList.add(new Favor("past1", "desc1", null,  null, 0));
        favorList.add(new Favor("past2", "desc2", null,  null, 0));
        favorList.add(new Favor("past3", "desc3", null,  null, 0));
        favorList.add(new Favor("past4", "desc4", null,  null, 0));
        favorList.add(new Favor("past5", "desc5", null,  null, 0));
        favorList.add(new Favor("past1", "desc1", null,  null, 0));
        favorList.add(new Favor("past2", "desc2", null,  null, 0));
        favorList.add(new Favor("past3", "desc3", null,  null, 0));
        favorList.add(new Favor("past4", "desc4", null,  null, 0));
        favorList.add(new Favor("past5", "desc5", null,  null, 0));
        favorList.add(new Favor("past1", "desc1", null,  null, 0));
        favorList.add(new Favor("past2", "desc2", null,  null, 0));
        favorList.add(new Favor("past3", "desc3", null,  null, 0));
        favorList.add(new Favor("past4", "desc4", null,  null, 0));
        favorList.add(new Favor("past5", "desc5", null,  null, 0));
        return favorList;
    }


    private ArrayList<Favor> getActiveFavorList() {
        ArrayList<Favor> favorList = new ArrayList<>();
        favorList.add(new Favor("active1", "desc1", null,  null, 0));
        favorList.add(new Favor("active2", "desc2", null,  null, 0));
        favorList.add(new Favor("active3", "desc3", null,  null, 0));
        favorList.add(new Favor("active4", "desc4", null,  null, 0));
        favorList.add(new Favor("active5", "desc5", null,  null, 0));
        favorList.add(new Favor("active1", "desc1", null,  null, 0));
        favorList.add(new Favor("active2", "desc2", null,  null, 0));
        favorList.add(new Favor("active3", "desc3", null,  null, 0));
        favorList.add(new Favor("active4", "desc4", null,  null, 0));
        favorList.add(new Favor("active5", "desc5", null,  null, 0));
        favorList.add(new Favor("active1", "desc1", null,  null, 0));
        favorList.add(new Favor("active2", "desc2", null,  null, 0));
        favorList.add(new Favor("active3", "desc3", null,  null, 0));
        favorList.add(new Favor("active4", "desc4", null,  null, 0));
        favorList.add(new Favor("active5", "desc5", null,  null, 0));
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

    // Replace the current fragment with the new fragment.
    // Todo: Seems useful. Try to put this method in a util package and import it here.
    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        //transaction.remove(this);
    }
}

