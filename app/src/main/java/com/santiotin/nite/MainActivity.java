package com.santiotin.nite;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
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

        mAuth = FirebaseAuth.getInstance();

        comprobarUsuario();

        //initialize the fragments
        initializeFragments();

        //getting bottom navigation view and attaching the listener
        BottomNavigationView bnavigation = findViewById(R.id.navigation);
        bnavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bnavigation.setSelectedItemId(R.id.navigation_home);



    }

    private void comprobarUsuario(){
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null || !user.isEmailVerified()) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        comprobarUsuario();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(ftoday, actual);
                    actual = 0;
                    break;

                case R.id.navigation_search:
                    loadFragment(fsearch, actual);
                    actual = 1;
                    break;

                case R.id.navigation_notifications:
                    loadFragment(fnotif, actual);
                    actual = 2;
                    break;

                case R.id.navigation_profile:
                    loadFragment(fprofile, actual);
                    actual = 3;
                    break;

                default:
                    loadFragment(ftoday, actual);
                    actual = 0;
                    break;
            }

            return true;
        }
    };

    private boolean loadFragment(Fragment fragment, int x) {
        //switching fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(x == 0) {
            ft.hide(ftoday);
        }
        else if(x == 1) {
            ft.hide(fsearch);
        }
        else if(x == 2) {
            ft.hide(fnotif);
        }
        else if(x == 3) {
            ft.hide(fprofile);
        }
        if (fragment != null) {
            ft.show(fragment).commit();
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

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, ftoday);
        ft.hide(ftoday);
        ft.add(R.id.fragment_container, fsearch);
        ft.hide(fsearch);
        ft.add(R.id.fragment_container, fnotif);
        ft.hide(fnotif);
        ft.add(R.id.fragment_container, fprofile);
        ft.hide(fprofile);
        ft.show(ftoday);
        ft.commit();
    }
}
