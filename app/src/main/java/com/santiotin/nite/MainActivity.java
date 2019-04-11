package com.santiotin.nite;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.santiotin.nite.Fragments.NotificationsFragment;
import com.santiotin.nite.Fragments.ProfileFragment;
import com.santiotin.nite.Fragments.SearchFragment;
import com.santiotin.nite.Fragments.TodayFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment ftoday;
    private Fragment fsearch;
    private Fragment fnotif;
    private Fragment fprofile;
    private int actual;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the fragments
        initializeFragments();
        //loading the default fragment
        loadFragment(ftoday);

        //getting bottom navigation view and attaching the listener
        BottomNavigationView bnavigation = findViewById(R.id.navigation);
        bnavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bnavigation.setSelectedItemId(R.id.navigation_home);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

       if (mAuth.getCurrentUser() == null) {

            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
       }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (actual == 0) {
                        ftoday = new TodayFragment();
                    }
                    actual = 0;
                    loadFragment(ftoday);
                    break;

                case R.id.navigation_search:
                    if (actual == 1) {
                        fsearch = new SearchFragment();
                    }
                    actual = 1;
                    loadFragment(fsearch);
                    break;

                case R.id.navigation_notifications:
                    if (actual == 2) {
                        fnotif = new NotificationsFragment();
                    }
                    actual = 2;
                    loadFragment(fnotif);
                    break;

                case R.id.navigation_profile:
                    if (actual == 3) {
                        fprofile = new ProfileFragment();
                    }
                    actual = 3;
                    loadFragment(fprofile);
                    break;

                default:
                    ftoday = new TodayFragment();
                    loadFragment(ftoday);
                    break;
            }

            return true;
        }
    };

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void initializeFragments(){
        actual = 0;
        ftoday = new TodayFragment();
        fsearch = new SearchFragment();
        fnotif = new NotificationsFragment();
        fprofile = new ProfileFragment();
    }
}
