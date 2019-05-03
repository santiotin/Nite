package com.santiotin.nite.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.santiotin.nite.EditProfileActivity;
import com.santiotin.nite.LoginActivity;
import com.santiotin.nite.MyEventsActivity;
import com.santiotin.nite.MyFriendsActivity;
import com.santiotin.nite.R;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int CHOOSE_IMAGE = 101 ;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView imgViewCircle;
    private Uri uriProfileImage;
    private String profileImageUrl;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //Foto de perfil
        imgViewCircle = view.findViewById(R.id.imgViewCircle);

        if (user != null){
            if (user.getPhotoUrl() != null) {
                Toast.makeText(getContext(), "Foto de perfil existente!", Toast.LENGTH_SHORT).show();

                Picasso.with(getContext())
                        .load(mAuth.getCurrentUser().getPhotoUrl().toString())
                        .into(imgViewCircle);
            }
            else{
                Toast.makeText(getContext(), "Logo!", Toast.LENGTH_SHORT).show();
                profileImageUrl = "android.resource://"+  getActivity().getPackageName() + "/" +  R.drawable.logo;
                uriProfileImage = Uri.parse(profileImageUrl);
                Picasso.with(getContext())
                        .load(uriProfileImage.toString())
                        .into(imgViewCircle);
            }
        }

        final ImageButton btnSettings = view.findViewById(R.id.btnSettings);
        TextView name = view.findViewById(R.id.name);


        RelativeLayout rlevents = view.findViewById(R.id.rlmyevents);
        RelativeLayout rlfollowing = view.findViewById(R.id.rlfollowing);
        RelativeLayout rlfollowers = view.findViewById(R.id.rlfollowers);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);

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
                startActivity(intent);
            }
        });

        rlfollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyFriendsActivity.class);
                startActivity(intent);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditProfileActivity.class);
                startActivity(i);
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
                                return true;

                            case R.id.config:
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        if(user != null && user.getDisplayName() != null && !user.getDisplayName().equals("")) {
            name.setText(user.getDisplayName());
        }

        return  view;

    }
}
