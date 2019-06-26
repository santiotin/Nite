package com.santiotin.nite.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiotin.nite.AssistantsFriendsActivity;
import com.santiotin.nite.EventDescriptionActivity;
import com.santiotin.nite.Holders.EventHolder;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Parsers.SnapshotParserEvent;
import com.santiotin.nite.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private View view;

    private ViewPager viewPager;
    private SearchEventsFragment searchEventsFragment;
    private SearchFriendsFragment searchFriendsFragment;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);


        final SearchView sv = view.findViewById(R.id.searchView);
        final ImageButton imgbtnsearch = view.findViewById(R.id.imgBtn_search);

        iniToolbar();
        iniViewPager();


        /*ImageView searchIcon = (ImageView)sv.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.ic_launcher_background);*/

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                createQueryAndSend(s);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {
                createQueryAndSend(s);
                return false;
            }
        });

        imgbtnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sv.hasFocus()) {
                    sv.setIconified(true);
                    sv.clearFocus();
                    if (viewPager.getCurrentItem() == 0) searchEventsFragment.iniAdapter();
                    else if (viewPager.getCurrentItem() == 1) searchFriendsFragment.iniAdapter();
                }
                else {
                    sv.setIconified(false);
                }


            }
        });


        sv.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imgbtnsearch.setImageResource(R.drawable.ic_back);

                } else {
                    imgbtnsearch.setImageResource(R.drawable.ic_search);
                }
            }

        });


        return view;
    }


    private void iniToolbar(){
        Toolbar myToolbar = view.findViewById(R.id.my_toolbarSearch);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void iniViewPager(){
        viewPager = view.findViewById(R.id.viewpagerSearch);
        setupViewPager();
        // Set Tabs inside Toolbar
        TabLayout tabs = view.findViewById(R.id.search_tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {

        SearchFragment.Adapter adapter = new SearchFragment.Adapter(getChildFragmentManager());
        searchEventsFragment = new SearchEventsFragment();
        searchFriendsFragment = new SearchFriendsFragment();
        adapter.addFragment(searchEventsFragment, getString(R.string.events));
        adapter.addFragment(searchFriendsFragment, getString(R.string.people));
        viewPager.setAdapter(adapter);

    }

    private void createQueryAndSend(String s){
        if (viewPager.getCurrentItem() == 0){
            Query query = FirebaseFirestore.getInstance()
                    .collection("events").whereArrayContains("searchNames", s);

            searchEventsFragment.getEventsOfQuery(query);
        }
        else if (viewPager.getCurrentItem() == 1){
            Query query = FirebaseFirestore.getInstance()
                    .collection("users").whereEqualTo("name", s);

            searchFriendsFragment.getUsersOfQuery(query);
        }

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
