package ch.epfl.favo.presenter.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ch.epfl.favo.R;

/**
 * View will contain list of favors requested in the past.
 * The list will contain clickable items that will expand
 * to give more information about them.
 * This object is a simple {@link Fragment} subclass.
 */
public class Tab2 extends Fragment {

    public Tab2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab2,
                container, false);
    }
}
