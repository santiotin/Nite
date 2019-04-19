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


    }

}
