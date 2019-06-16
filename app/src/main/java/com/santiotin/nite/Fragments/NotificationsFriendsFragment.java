package com.santiotin.nite.Fragments;


import android.os.Bundle;
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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiotin.nite.Holders.RequestHolder;
import com.santiotin.nite.Models.Request;
import com.santiotin.nite.Parsers.SnapshotParserRequest;
import com.santiotin.nite.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFriendsFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter fbAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private View view;

    private ImageView noRequestsImage;
    private TextView noRequestsText;


    public NotificationsFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notifications_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        noRequestsImage = view.findViewById(R.id.imgNoRequests);
        noRequestsText = view.findViewById(R.id.tvNoNotif);


        iniRecyclerView();
        getMyRequests();


        return view;
    }

    public void iniRecyclerView(){
        mRecyclerView = view.findViewById(R.id.recyclerViewNotifFriends);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);

        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void getMyRequests(){

        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("requests")
                .orderBy("personName");

        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(query, new SnapshotParserRequest())
                .build();

        fbAdapter = new FirestoreRecyclerAdapter<Request, RequestHolder>(options) {
            @Override
            public RequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_request, parent, false);

                return new RequestHolder(view);
            }


            @Override
            protected void onBindViewHolder(RequestHolder holder, final int position, final Request r) {
                holder.setName(r.getPersonName());
                holder.setDate(r.getDay(), r.getMonth(), r.getYear());
                holder.setImage(getContext(), r.getPersonId());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() > 0){
                    noRequestsImage.setVisibility(View.INVISIBLE);
                    noRequestsText.setVisibility(View.INVISIBLE);
                    Log.d("control", "notEmpty");
                }else{
                    noRequestsImage.setVisibility(View.VISIBLE);
                    noRequestsText.setVisibility(View.VISIBLE);
                    Log.d("control", "isEmpty");
                }
            }
        };



        mRecyclerView.setAdapter(fbAdapter);
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
