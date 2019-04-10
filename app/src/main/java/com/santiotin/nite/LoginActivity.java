package com.santiotin.nite;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.santiotin.nite.Fragments.SignInFragment;
import com.santiotin.nite.Fragments.SignUpFragment;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final CustomViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);
        viewPager.disableScroll(true);
        viewPager.invalidate();

        final TextView tvSignIn = findViewById(R.id.tvSignIn);
        final TextView tvSignUp = findViewById(R.id.tvSignUp);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() == 1){
                    tvSignIn.setTextColor(getResources().getColor(R.color.pink));
                    tvSignIn.setTextSize(40);
                    tvSignUp.setTextColor(getResources().getColor(R.color.textcolor));
                    tvSignUp.setTextSize(30);
                    viewPager.setCurrentItem(0);
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() == 0) {
                    tvSignIn.setTextColor(getResources().getColor(R.color.textcolor));
                    tvSignIn.setTextSize(30);
                    tvSignUp.setTextColor(getResources().getColor(R.color.pink));
                    tvSignUp.setTextSize(40);
                    viewPager.setCurrentItem(1);
                }
            }
        });
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
