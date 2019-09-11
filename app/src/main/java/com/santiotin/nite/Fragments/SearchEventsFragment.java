package com.santiotin.nite.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiotin.nite.Activities.EventDescriptionActivity;
import com.santiotin.nite.Holders.EventSearchHolder;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Parsers.SnapshotParserEvent;
import com.santiotin.nite.R;

public class SearchEventsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter fbAdapter;
    private View view;



    public SearchEventsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_events, container, false);

        iniRecyclerView();
        iniAdapter();


        return view;
    }

    public void iniRecyclerView(){
        mRecyclerView = view.findViewById(R.id.recyclerSearchViewEvents);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);

        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void iniAdapter(){
        Query query = FirebaseFirestore.getInstance()
                .collection("events");
        getEventsOfQuery(query);
    }

    public void getEventsOfQuery(Query query){

        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, new SnapshotParserEvent())
                .build();

        fbAdapter = new FirestoreRecyclerAdapter<Event, EventSearchHolder>(options) {
            @Override
            public EventSearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_event_search, parent, false);

                return new EventSearchHolder(view);
            }


            @Override
            protected void onBindViewHolder(EventSearchHolder holder, final int position, final Event e) {
                holder.setTitle(e.getName(), e.getClub());
                holder.setDate(e.getDay(),e.getMonth(),e.getYear());
                holder.setFondo(getContext(), e.getId());

                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), EventDescriptionActivity.class);
                        intent.putExtra("event", e);
                        startActivity(intent);
                    }
                });
            }

        };



        if (mRecyclerView != null)mRecyclerView.setAdapter(fbAdapter);
        if (fbAdapter != null)fbAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (fbAdapter != null) fbAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fbAdapter != null) fbAdapter.stopListening();
    }
}
