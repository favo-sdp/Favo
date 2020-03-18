package ch.epfl.favo.view;

import android.widget.RadioButton;

public interface ViewController {
    void hideBottomTabs();
    void showBottomTabs();
    void showBurgerIcon();
    void showBackIcon();
    void checkMapViewButton();
    void checkFavListViewButton();
    void setupViewTopDestTab();
    void setupViewBotDestTab();
}
