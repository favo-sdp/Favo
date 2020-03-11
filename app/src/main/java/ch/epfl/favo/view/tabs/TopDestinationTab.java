package ch.epfl.favo.view.tabs;

import androidx.fragment.app.Fragment;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.view.ViewController;

public abstract class TopDestinationTab extends Fragment {
    public void setupView(){
        ((ViewController) getActivity()).showBurgerIcon();
        ((ViewController) getActivity()).showBottomTabs();
    }
    public void checkMapButton(){
        ((ViewController) getActivity()).checkMapViewButton();
    }
    public void checkFavListButton(){
        ((ViewController) getActivity()).checkFavListViewButton();
    }

}
