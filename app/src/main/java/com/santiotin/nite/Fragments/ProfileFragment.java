package com.santiotin.nite.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.EditProfileActivity;
import com.santiotin.nite.LoginActivity;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.MyEventsActivity;
import com.santiotin.nite.MyFriendsActivity;
import com.santiotin.nite.Parsers.SnapshotParserUser;
import com.santiotin.nite.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
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

        iniUserImage();
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
                PopupMenu popupMenu = new PopupMenu(getActivity(), btnSettings);
                popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.logout:
                                logOut();
                                return true;

                            case R.id.config:
                                return true;

                            case R.id.btnEditProfile:
                                Intent i = new Intent(getContext(), EditProfileActivity.class);
                                i.putExtra("user", mUser);
                                startActivity(i);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
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
                .error(R.drawable.logo)
                .into(imageView);

    }

    @Override
    public void onStart() {
        super.onStart();
        iniUserImage();
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
                    iniCampos();

                } else {
                    Log.d("control", "Current data: null");
                }
            }
        });
    }

    private void logOut(){
        mAuth.signOut();

        //sign out de la cuenta de google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        mGoogleSignInClient.signOut();

        //finalizar activity e ir al login
        getActivity().finish();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
