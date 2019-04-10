package com.santiotin.nite;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.santiotin.nite.Fragments.SignInFragment;
import com.santiotin.nite.Fragments.SignUpFragment;

import java.util.ArrayList;
import java.util.List;

public class PruebasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pruebas);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);


    }


    private void setupViewPager(ViewPager viewPager) {

        PruebasActivity.Adapter adapter = new PruebasActivity.Adapter(getSupportFragmentManager());
        adapter.addFragment(new SignInFragment(), getString(R.string.events));
        adapter.addFragment(new SignUpFragment(), getString(R.string.friends));
        viewPager.setAdapter(adapter);


    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
