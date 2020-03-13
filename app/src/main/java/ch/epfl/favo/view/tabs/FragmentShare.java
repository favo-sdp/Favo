package ch.epfl.favo.view.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.epfl.favo.R;


/**
 * A simple {@link Fragment} subclass.
 */

public class FragmentShare extends BottomDestinationTab {

    public FragmentShare() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setupView();
        return inflater.inflate(R.layout.fragment_share, container, false);
    }
}