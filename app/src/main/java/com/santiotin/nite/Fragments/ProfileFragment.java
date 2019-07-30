package com.santiotin.nite.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.EditProfileActivity;
import com.santiotin.nite.Holders.HistoryEventHolder;
import com.santiotin.nite.Holders.NotRequestHolder;
import com.santiotin.nite.LoginActivity;
import com.santiotin.nite.Models.HistoryEvent;
import com.santiotin.nite.Models.NotRequest;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.MyEventsActivity;
import com.santiotin.nite.MyFriendsActivity;
import com.santiotin.nite.Parsers.SnapshotParserHistoryEvent;
import com.santiotin.nite.Parsers.SnapshotParserNotRequest;
import com.santiotin.nite.Parsers.SnapshotParserUser;
import com.santiotin.nite.R;
import com.santiotin.nite.SettingsActivity;
import com.subinkrishna.widget.CircularImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter fbAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser fbUser;
    private User mUser;
    private FirebaseFirestore db;
    private CircularImageView imageView;
    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mUser = null;
        imageView = view.findViewById(R.id.imgViewCircle);

        listenUser();


        final ImageButton btnSettings = view.findViewById(R.id.btnSettings);


        RelativeLayout rlevents = view.findViewById(R.id.rlmyevents);
        RelativeLayout rlfollowing = view.findViewById(R.id.rlfollowing);
        RelativeLayout rlfollowers = view.findViewById(R.id.rlfollowers);

        rlevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyEventsActivity.class);
                startActivity(intent);
            }
        });

        rlfollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyFriendsActivity.class);
                intent.putExtra("bool", true);
                startActivity(intent);
            }
        });

        rlfollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyFriendsActivity.class);
                intent.putExtra("bool", false);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SettingsActivity.class);
                i.putExtra("user", mUser);
                startActivity(i);
            }
        });



        return  view;

    }

    private void iniCampos(){
        TextView name = view.findViewById(R.id.name);
        TextView city = view.findViewById(R.id.city);
        TextView age = view.findViewById(R.id.age);
        TextView mEvents = view.findViewById(R.id.numEvents);
        TextView nFollowers = view.findViewById(R.id.numfollowers);
        TextView nFollowing = view.findViewById(R.id.numfollowing);
        TextView textEvents = view.findViewById(R.id.textEvents);
        TextView textFollowers = view.findViewById(R.id.textFollowers);
        TextView textFollowing = view.findViewById(R.id.textFollowing);


        if (mUser != null){
            name.setText(mUser.getName());
            city.setText(mUser.getCity());
            age.setText(mUser.getAge());
            mEvents.setText(String.valueOf(mUser.getNumEvents()));
            nFollowers.setText(String.valueOf(mUser.getNumFollowers()));
            nFollowing.setText(String.valueOf(mUser.getNumFollowing()));

            if(mUser.getNumEvents() == 1){
                textEvents.setText(getString(R.string.event));
            }else {
                textEvents.setText(getString(R.string.events));
            }

            if (mUser.getNumFollowers() == 1){
                textFollowers.setText(getString(R.string.follower));
            }else {
                textFollowers.setText(getString(R.string.followers));
            }

            if (mUser.getNumFollowing() == 1){
                textFollowing.setText(getString(R.string.following1));
            }else {
                textFollowing.setText(getString(R.string.following));
            }

        }else {
            name.setText(fbUser.getDisplayName());
        }


    }

    private void iniUserImage(){

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + fbUser.getUid() + ".jpg");
        GlideApp.with(getContext())
                .load(storageRef)
                .signature(new ObjectKey(mUser.getPhotoTime()))
                .error(R.drawable.logo)
                .into(imageView);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mUser != null) iniUserImage();
        if (fbAdapter != null) fbAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (fbAdapter != null) fbAdapter.stopListening();
    }

    private void listenUser() {
        final DocumentReference docRef = db.collection("users").document(fbUser.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("control", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("control", "Current data: " + snapshot.getData());
                    SnapshotParserUser spu = new SnapshotParserUser();
                    mUser = spu.parseSnapshot(snapshot);
                    iniRecyclerView();
                    iniCampos();
                    iniUserImage();

                } else {
                    Log.d("control", "Current data: null");
                }
            }
        });
    }

    public void iniRecyclerView(){
        mRecyclerView = view.findViewById(R.id.recyclerViewHistoryEvents);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);

        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);

        getMyHistoryEvents();
    }

    public void getMyHistoryEvents(){

        Log.d("control", "entroo");
        final TextView tvHistoryEvents = view.findViewById(R.id.tvActivityHistoryEvents);
        final ImageView imgViewNoActivity = view.findViewById(R.id.imgViewProfileActivity);
        imgViewNoActivity.setVisibility(View.INVISIBLE);

        Log.d("control", fbUser.getUid());
        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .document(fbUser.getUid())
                .collection("historyEvents")
                .orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<HistoryEvent> options = new FirestoreRecyclerOptions.Builder<HistoryEvent>()
                .setQuery(query, new SnapshotParserHistoryEvent())
                .build();

        fbAdapter = new FirestoreRecyclerAdapter<HistoryEvent, HistoryEventHolder>(options) {
            @Override
            public HistoryEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_history_events, parent, false);

                return new HistoryEventHolder(view);
            }


            @Override
            protected void onBindViewHolder(HistoryEventHolder holder, final int position, final HistoryEvent he) {
                String txt = "Te has apuntado a: " + he.getEventTitle();
                holder.setName(txt);
                holder.setDate(he.getDay(), he.getMonth(), he.getYear());
                holder.setImage(getContext(), he.getEventId());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() > 0){
                    imgViewNoActivity.setVisibility(View.INVISIBLE);
                    tvHistoryEvents.setVisibility(View.INVISIBLE);
                    Log.d("control", "notEmpty");
                }else{
                    imgViewNoActivity.setVisibility(View.VISIBLE);
                    tvHistoryEvents.setVisibility(View.VISIBLE);
                    Log.d("control", "isEmpty");
                }
            }
        };



        mRecyclerView.setAdapter(fbAdapter);
        fbAdapter.startListening();
    }
}
