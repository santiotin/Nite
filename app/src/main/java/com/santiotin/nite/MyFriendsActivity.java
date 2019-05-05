package com.santiotin.nite;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.santiotin.nite.Fragments.NotificationsEventsFragment;
import com.santiotin.nite.Fragments.NotificationsFragment;
import com.santiotin.nite.Fragments.NotificationsFriendsFragment;

import java.util.ArrayList;
import java.util.List;

public class MyFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);

        iniToolbar();

        // Setting ViewPager for each Tabs
        ViewPager viewPager = findViewById(R.id.viewpagerFriends);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = findViewById(R.id.friendsTabLayout);
        tabs.setupWithViewPager(viewPager);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return true;
    }

    private void iniToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.myFriends));
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {

        MyFriendsActivity.Adapter adapter = new MyFriendsActivity.Adapter(getSupportFragmentManager());
        adapter.addFragment(new NotificationsEventsFragment(), getString(R.string.followers));
        adapter.addFragment(new NotificationsFriendsFragment(), getString(R.string.following));
        viewPager.setAdapter(adapter);

        Boolean b = getIntent().getBooleanExtra("bool", true);

        if(b){
            viewPager.setCurrentItem(0);
        }else{
            viewPager.setCurrentItem(1);
        }


    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
