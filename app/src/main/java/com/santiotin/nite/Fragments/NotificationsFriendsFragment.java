package com.santiotin.nite.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiotin.nite.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFriendsFragment extends Fragment {


    public NotificationsFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications_friends, container, false);
    }

}
