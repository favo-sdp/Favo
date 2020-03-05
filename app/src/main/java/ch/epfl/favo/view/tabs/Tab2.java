package ch.epfl.favo.view.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.epfl.favo.R;
import ch.epfl.favo.view.tabs.F.favor;
import ch.epfl.favo.view.tabs.addFavor.favor_added;

/**
 * View will contain list of favors requested in the past.
 * The list will contain clickable items that will expand
 * to give more information about them.
 * This object is a simple {@link Fragment} subclass.
 */
public class Tab2 extends Fragment implements View.OnClickListener {

    public Tab2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tab2, container, false);
        Button newFavorBtn = (Button) rootView.findViewById(R.id.new_favor);
        newFavorBtn.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.new_favor:
                fragment = new favor();
                replaceFragment(fragment);
                break;
        }
    }

    public void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tab2, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
