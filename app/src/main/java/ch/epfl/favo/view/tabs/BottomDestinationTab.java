package ch.epfl.favo.view.tabs;

import androidx.fragment.app.Fragment;

import ch.epfl.favo.view.ViewController;

public abstract class BottomDestinationTab extends Fragment {
    public void setupView(){
        ((ViewController) getActivity()).showBackIcon();
        ((ViewController) getActivity()).hideBottomTabs();
    }
}
