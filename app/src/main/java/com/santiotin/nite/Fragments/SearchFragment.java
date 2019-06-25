package com.santiotin.nite.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private View view;
    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter fbAdapter;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.my_toolbarSearch);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        final SearchView sv = view.findViewById(R.id.searchView);
        final ImageButton imgbtnsearch = (ImageButton) view.findViewById(R.id.imgBtn_search);
        iniRecyclerView();


        /*ImageView searchIcon = (ImageView)sv.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.ic_launcher_background);*/

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Query query = FirebaseFirestore.getInstance()
                        .collection("events").whereArrayContains("searchNames", s);
                getAllEvents(query);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {
                Query query = FirebaseFirestore.getInstance()
                        .collection("events").whereArrayContains("searchNames", s);
                getAllEvents(query);
                return false;
            }
        });

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


        Query query = FirebaseFirestore.getInstance()
                .collection("events");
        getAllEvents(query);

        return view;
    }

    private void searchElement(String newText) {
        Toast.makeText(getContext(), "Entramos searchElement", Toast.LENGTH_SHORT).show();
        Query query = FirebaseFirestore.getInstance()
                .collection("events").whereEqualTo("day", newText);

        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, new SnapshotParserEvent())
                .build();

        fbAdapter = new FirestoreRecyclerAdapter<Event, EventHolder>(options) {
            @Override
            public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_event, parent, false);

                return new EventHolder(view);
            }


            @Override
            protected void onBindViewHolder(EventHolder holder, final int position, final Event e) {
                holder.setTitle(e.getClub() + ": " + e.getName());
                holder.setNumAssists(e.getNumAssistants());
                holder.setFondo(getContext(), e.getId());

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), EventDescriptionActivity.class);
                        intent.putExtra("event", e);
                        startActivity(intent);
                    }
                });

                holder.btnFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), AssistantsFriendsActivity.class);
                        intent.putExtra("event", e);
                        startActivity(intent);
                    }
                });
            }

        };

        mRecyclerView.setAdapter(fbAdapter);
        fbAdapter.startListening();
    }


    public void iniRecyclerView(){
        mRecyclerView = view.findViewById(R.id.recyclerSearchView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);

        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void getAllEvents(Query query){



        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, new SnapshotParserEvent())
                .build();

        fbAdapter = new FirestoreRecyclerAdapter<Event, EventHolder>(options) {
            @Override
            public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_event, parent, false);

                return new EventHolder(view);
            }


            @Override
            protected void onBindViewHolder(EventHolder holder, final int position, final Event e) {
                holder.setTitle(e.getClub() + ": " + e.getName());
                holder.setNumAssists(e.getNumAssistants());
                holder.setFondo(getContext(), e.getId());

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), EventDescriptionActivity.class);
                        intent.putExtra("event", e);
                        startActivity(intent);
                    }
                });

                holder.btnFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), AssistantsFriendsActivity.class);
                        intent.putExtra("event", e);
                        startActivity(intent);
                    }
                });
            }

        };



        mRecyclerView.setAdapter(fbAdapter);
        fbAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        fbAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        fbAdapter.stopListening();
    }
}
