package ch.epfl.favo.presenter;

import ch.epfl.favo.presenter.tabs.Tab1;
import ch.epfl.favo.presenter.tabs.Tab2;
import ch.epfl.favo.presenter.tabs.Tab3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * This adapter class will ensure the fragments are injected in the view when the
 * correct tab is selected.
 */
public class TabAdapter extends FragmentStateAdapter {

    //Constructor using Fragment Manager
    public TabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /**
     * Adaptor produces a fragment when tab is active.
     * @param position corresponds to current tab
     * @return a Fragment from the tabs
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0: return new Tab1();
            case 1: return new Tab2();
            case 2: return new Tab3();
            default: return null;
        }
    }

    /**
     * Get the number of tabs.
     * @return number of tabs.
     */
    @Override
    public int getItemCount() {
        return 3;
    }
}



