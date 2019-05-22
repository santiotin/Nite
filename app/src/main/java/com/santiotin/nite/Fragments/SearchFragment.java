package com.santiotin.nite.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.santiotin.nite.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        final SearchView sv = view.findViewById(R.id.searchView);
        final ImageButton imgbtnsearch = (ImageButton) view.findViewById(R.id.imgBtn_search);



        /*ImageView searchIcon = (ImageView)sv.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.ic_launcher_background);*/

        imgbtnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sv.hasFocus()) {
                    sv.setIconified(true);
                    sv.clearFocus();
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

}
