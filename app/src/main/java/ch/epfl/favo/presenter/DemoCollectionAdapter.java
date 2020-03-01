package ch.epfl.favo.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DemoCollectionAdapter extends FragmentPagerAdapter {
    public DemoCollectionAdapter(FragmentManager fm) {

        super(fm);
    }

   //@NonNull
   //@Override
   //public Fragment createFragment(int position) {
   //    Fragment returnFragment = new MapFragment();
   //    switch (position) {
   //        case 0:
   //        case 1:
   //            returnFragment = new FavorListFragment();
   //        case 2:
   //            returnFragment = new AccountInfoFragment();
   //        //TODO: FIgure out a way to increase test coverage
   //    }
   //    return returnFragment;
   //}


   //@Override
   //public int getItemCount() {
   //    return 3;
   //}

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment returnFragment = new MapFragment();
        switch (position) {
            case 0:
            case 1:
                returnFragment = new FavorListFragment();
            case 2:
                returnFragment = new AccountInfoFragment();
                //TODO: FIgure out a way to increase test coverage
        }
        return returnFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}



