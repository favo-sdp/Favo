package ch.epfl.favo.presenter.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.epfl.favo.R;

/**
 * View will contain a map and a favor request pop-up.
 * It is implemented using the {@link Fragment} subclass.
 */
public class Tab1 extends Fragment {

    public Tab1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab1, container, false);
    }
}
