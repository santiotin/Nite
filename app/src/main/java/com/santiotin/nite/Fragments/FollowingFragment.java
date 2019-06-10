package com.santiotin.nite.Fragments;


import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.RVFriendsSmallAdapter;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.PersonProfileActivity;
import com.santiotin.nite.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private StorageReference storageRef;


    public FollowingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();

        iniRecyclerView(view);
        getFollowing();


        return view;
    }

    public void iniRecyclerView(View v){

        mRecyclerView = v.findViewById(R.id.recyclerViewFollowing);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);
        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);


    }

    public void actualizarAdapter(List<User> users){
        RecyclerView.Adapter mAdapter = new RVFriendsSmallAdapter(users, R.layout.item_friend, new RVFriendsSmallAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User u, int position) {
                Intent intent = new Intent(getContext(), PersonProfileActivity.class);
                intent.putExtra("user", u);
                startActivity(intent);
            }
        }, getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void getFollowing(){

        final List<User> users = new ArrayList<>();

        db.collection("users")
                .document(user.getUid())
                .collection("following")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   if (task.getResult().isEmpty()) {
                                                       actualizarAdapter(users);
                                                       Log.d("control", "Empty ", task.getException());
                                                   }else {
                                                       for (final QueryDocumentSnapshot document : task.getResult()) {
                                                           Log.d("control", "Recibo Seguido", task.getException());
                                                           users.add(new User(
                                                                   document.getId(),
                                                                   document.getString("followingName")));
                                                           actualizarAdapter(users);
                                                           //users.add(new User(document.getString("userName"), R.drawable.logo));
                                                           Log.d("control", String.valueOf(users.size()), task.getException());

                                                       }
                                                   }

                                               } else {
                                                   Log.d("control", "Error getting documents: ", task.getException());
                                               }
                                           }
                                       }
                );
    }

}
