package com.santiotin.nite;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.RVFriendsSmallAdapter;
import com.santiotin.nite.Adapters.UserHolder;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Models.User;

import java.util.ArrayList;
import java.util.List;

public class AssistantsFriendsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Event event;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistants);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        event = (Event) getIntent().getSerializableExtra("event");

        progressBar = findViewById(R.id.progressBarAssistants);

        iniToolbar();
        iniRecyclerView();
        getUserFriends();



    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return true;
    }

    public void iniToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.friends));
    }

    public void getUserFriends(){

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
                                                           Log.d("control", "Recibo Seguidos", task.getException());
                                                           db.collection("events")
                                                                   .document(event.getId())
                                                                   .collection("assistingUsers")
                                                                   .document(document.getId())
                                                                   .get()
                                                                   .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                           if (task.isSuccessful()) {
                                                                               final DocumentSnapshot documentEvent = task.getResult();
                                                                               if (documentEvent.exists()) {
                                                                                   users.add(new User(
                                                                                           documentEvent.getId(),
                                                                                           documentEvent.getString("userName")));
                                                                                   actualizarAdapter(users);
                                                                                   Log.d("control", "DocumentSnapshot data: " + documentEvent.getData());
                                                                               } else {
                                                                                   actualizarAdapter(users);
                                                                                   Log.d("control", "No such document");
                                                                               }
                                                                           } else {
                                                                               Log.d("control", "get failed with ", task.getException());
                                                                               actualizarAdapter(users);
                                                                           }
                                                                       }
                                                                   });
                                                       }
                                                   }

                                               } else {
                                                   Log.d("control", "Error getting documents: ", task.getException());
                                               }
                                           }
                                       }
                );
    }

    public void iniRecyclerView(){
        progressBar.setVisibility(View.VISIBLE);

        mRecyclerView = findViewById(R.id.recyclerViewAssists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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
                if (!u.getUid().equals(user.getUid())){
                    Intent intent = new Intent(getApplicationContext(), PersonProfileActivity.class);
                    intent.putExtra("user", u);
                    startActivity(intent);
                }
            }
        }, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

}
