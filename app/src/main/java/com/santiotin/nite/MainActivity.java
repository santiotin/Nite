package com.santiotin.nite;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.santiotin.nite.Fragments.NotificationsFragment;
import com.santiotin.nite.Fragments.ProfileFragment;
import com.santiotin.nite.Fragments.SearchFragment;
import com.santiotin.nite.Fragments.TodayFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment ftoday;
    private Fragment fsearch;
    private Fragment fnotif;
    private Fragment fprofile;
    private Fragment active;

    FragmentManager fm;

    private FirebaseAuth mAuth;
    private BottomNavigationView bnavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        comprobarUsuario();

        //initialize the fragments
        fm = getSupportFragmentManager();
        initializeFragments();

        //getting bottom navigation view and attaching the listener
        bnavigation = findViewById(R.id.navigation);
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

            if (active == fnotif) removeBadge();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(ftoday).commit();
                    active = ftoday;
                    break;

                case R.id.navigation_search:
                    fm.beginTransaction().hide(active).show(fsearch).commit();
                    active = fsearch;
                    break;

                case R.id.navigation_notifications:
                    fm.beginTransaction().hide(active).show(fnotif).commit();
                    active = fnotif;
                    removeBadge();
                    break;

                case R.id.navigation_profile:
                    fm.beginTransaction().hide(active).show(fprofile).commit();
                    active = fprofile;
                    break;

                default:
                    fm.beginTransaction().hide(active).show(ftoday).commit();
                    active = ftoday;
                    break;
            }

            return true;
        }
    };

    private void initializeFragments(){

        ftoday = new TodayFragment();
        fsearch = new SearchFragment();
        fnotif = new NotificationsFragment();
        fprofile = new ProfileFragment();

        active = ftoday;

        fm.beginTransaction().add(R.id.fragment_container, fprofile, "profile").hide(fprofile).commit();
        fm.beginTransaction().add(R.id.fragment_container, fnotif, "notif").hide(fnotif).commit();
        fm.beginTransaction().add(R.id.fragment_container, fsearch, "search").hide(fsearch).commit();
        fm.beginTransaction().add(R.id.fragment_container, ftoday, "today").commit();

    }

    public void showBadge() {
        removeBadge();
        BottomNavigationItemView itemView = bnavigation.findViewById(R.id.navigation_notifications);
        View badge = LayoutInflater.from(getApplicationContext()).inflate(R.layout.notifiaction_badge, bnavigation, false);
        itemView.addView(badge);
    }

    public void removeBadge() {
        BottomNavigationItemView itemView = bnavigation.findViewById(R.id.navigation_notifications);
        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(2);
        }
    }
}
