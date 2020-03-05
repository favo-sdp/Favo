package ch.epfl.favo.view.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.epfl.favo.R;
import ch.epfl.favo.presenter.tabs.addFavor.FavorRequestView;

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

        Button newFavorBtn = rootView.findViewById(R.id.new_favor);
        newFavorBtn.setOnClickListener(this);

        return rootView;
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
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    // Replace the current fragment with the new fragment.
    // Todo: Seems useful. Try to put this method in a util package and import it here.
    private void replaceFragment(Fragment newFragment) {
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tab2, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
