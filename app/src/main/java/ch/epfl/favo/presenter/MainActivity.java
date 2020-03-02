package ch.epfl.favo.presenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import ch.epfl.favo.R;
import ch.epfl.favo.models.FavorUtil;
import ch.epfl.favo.models.UserUtil;

/**
 * This will control the general view of our app. It will contain 3 tabs. On the first tab it will have the map
 * and the favor request pop-up. On the second tab it will contain the list view of previous favors. On the third tab
 * it will contain account information. These tabs will be implemented in more detail in the other presenter classes
 */
public class MainActivity extends AppCompatActivity {
    // Model references
    private UserUtil userUtility = UserUtil.getSingleInstance();
    private FavorUtil favorUtility = FavorUtil.getSingleInstance();

    // UI
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TabAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViewPager();

    }

    private void setUpViewPager() {
        //Instantiate UI elements
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        //set pager orientation
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        myAdapter = new TabAdapter(getSupportFragmentManager(), getLifecycle());
        //hook adapter to view pager
        viewPager.setAdapter(myAdapter);
        viewPager.setPageTransformer(new MarginPageTransformer(1500));

        //Create mediator
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
            @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: {
                        tab.setIcon(R.drawable.ic_group_work_24px);
                        break;
                    }
                    case 1: {
                        tab.setIcon(R.drawable.ic_list_24px);
                        break;
                    }
                    case 2: {
                        tab.setIcon(R.drawable.ic_face_24px);
                        break;
                    }
                }
            }
        }).attach();
    }


}


