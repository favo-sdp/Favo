package ch.epfl.favo.presenter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
    TabItem mapView;
    TabItem listView;
    TabItem accView;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Instantiate UI elements
        tabLayout = findViewById(R.id.tab_layout);
        mapView = findViewById(R.id.tab_map);
        listView = findViewById(R.id.tab_list);
        accView = findViewById(R.id.tab_account);
        viewPager = findViewById(R.id.pager);

        DemoCollectionAdapter adapter = new DemoCollectionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

            //Also possible to use tabs
        //new TabLayoutMediator(tabLayout, viewPager,
        //        (tab, position) -> tab.setText("OBJECT " + (position + 1))
        //).attach();



        //Intent intent = new Intent(this, DemoObjectFragment.class);
        //startActivity(intent);
    }




}


