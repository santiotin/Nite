package com.santiotin.nite.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiotin.nite.Holders.NotEventHolder;
import com.santiotin.nite.Holders.NotRequestHolder;
import com.santiotin.nite.MainActivity;
import com.santiotin.nite.Models.NotEvent;
import com.santiotin.nite.Models.NotRequest;
import com.santiotin.nite.Parsers.SnapshotParserNotEvent;
import com.santiotin.nite.Parsers.SnapshotParserNotRequest;
import com.santiotin.nite.R;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsEventsFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter fbAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private View view;

    private ImageView noRequestsImage;
    private TextView noRequestsText;

    private boolean firstNotification;
    private int total;


    public NotificationsEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notifications_events, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        noRequestsImage = view.findViewById(R.id.imgNotFriendEvents);
        noRequestsText = view.findViewById(R.id.tvNotFriendEvents);

        firstNotification = true;
        total = 0;


        iniRecyclerView();
        getMyNotFriends();

        return view;
    }

    public void iniRecyclerView(){
        mRecyclerView = view.findViewById(R.id.recyclerViewNotFriendEvent);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);

        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void getMyNotFriends(){

        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("notFriendEvents")
                .orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<NotEvent> options = new FirestoreRecyclerOptions.Builder<NotEvent>()
                .setQuery(query, new SnapshotParserNotEvent())
                .build();

        fbAdapter = new FirestoreRecyclerAdapter<NotEvent, NotEventHolder>(options) {
            @Override
            public NotEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_friend_event, parent, false);

                return new NotEventHolder(view);
            }


            @Override
            protected void onBindViewHolder(NotEventHolder holder, final int position, final NotEvent ne) {
                holder.setName(ne.getPersonName(), ne.getEventTitle());
                holder.setDate(ne.getDay(), ne.getMonth(), ne.getYear());
                holder.setImagePerson(getContext(), ne.getPersonId());
                holder.setImageEvent(getContext(), ne.getEventId());
            }

            @Override
            public void onDataChanged() {

                if (getItemCount() > 0){
                    noRequestsImage.setVisibility(View.INVISIBLE);
                    noRequestsText.setVisibility(View.INVISIBLE);
                    Log.d("control", "notEmpty");

                    if (firstNotification) {
                        firstNotification = false;
                        total = getItemCount();
                    }
                    else if (total != getItemCount()){
                        total = getItemCount();
                        showBadgeFromBottomBar();
                    }

                }else{
                    noRequestsImage.setVisibility(View.VISIBLE);
                    noRequestsText.setVisibility(View.VISIBLE);
                    Log.d("control", "isEmpty");
                }
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);

                if (type == ChangeEventType.CHANGED) showBadgeFromBottomBar();
            }
        };



        mRecyclerView.setAdapter(fbAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        fbAdapter.startListening();

        removeBagdeFromBottomBar();

    }

    @Override
    public void onStop() {
        super.onStop();
        fbAdapter.stopListening();
    }

    public void removeBagdeFromBottomBar(){
        Activity ma = getActivity();
        if (ma instanceof MainActivity){
            ((MainActivity)ma).removeBadge();
        }
    }

    public void showBadgeFromBottomBar(){
        Activity ma = getActivity();
        if (ma instanceof MainActivity){
            ((MainActivity)ma).showBadge();
        }
    }

}
