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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiotin.nite.Holders.UserHolder;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.Parsers.SnapshotParserUser;
import com.santiotin.nite.PersonProfileActivity;
import com.santiotin.nite.R;

public class SearchFriendsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter fbAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private View view;


    public SearchFriendsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        iniRecyclerView();
        iniAdapter();

        return view;
    }

    public void iniRecyclerView(){
        mRecyclerView = view.findViewById(R.id.recyclerSearchViewFriends);
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
                .collection("users");
        getUsersOfQuery(query);
    }

    public void getUsersOfQuery(Query query){

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, new SnapshotParserUser())
                .build();


        fbAdapter = new FirestoreRecyclerAdapter<User, UserHolder>(options) {
            @Override
            public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_friend, parent, false);

                return new UserHolder(view);
            }


            @Override
            protected void onBindViewHolder(UserHolder holder, final int position, final User u) {
                holder.setName(u.getName());
                holder.setImage(getContext(), u.getUid(), u.getPhotoTime());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!u.getUid().equals(user.getUid())){
                            Intent intent = new Intent(getContext(), PersonProfileActivity.class);
                            intent.putExtra("user", u);
                            startActivity(intent);
                        }
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
        if (fbAdapter != null) fbAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fbAdapter != null) fbAdapter.stopListening();
    }

}
