package ch.epfl.favo.presenter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ch.epfl.favo.presenter.tabs.Tab1;
import ch.epfl.favo.presenter.tabs.Tab2;
import ch.epfl.favo.presenter.tabs.Tab3;

public class TabAdapter extends FragmentStateAdapter {


    public TabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0: return new Tab1();
            case 1: return new Tab2();
            case 2: return new Tab3();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}



