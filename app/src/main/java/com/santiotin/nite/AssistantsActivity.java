package com.santiotin.nite;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.RVFriendsSmallAdapter;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Models.User;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;

public class AssistantsActivity extends AppCompatActivity {

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

        progressBar = findViewById(R.id.progressBarAssistants);

        iniToolbar();
        iniRecyclerView();
        getUsers();



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
        getSupportActionBar().setTitle(getString(R.string.Participants));
    }

    public void getUsers(){

        event = (Event) getIntent().getSerializableExtra("event");

        final List<User> users = new ArrayList<>();

        db.collection("assistants")
                .whereEqualTo("eid", event.getId())
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
                                                           Log.d("control", "Recibo Assistente", task.getException());
                                                           storageRef.child("profilepics/" + document.getString("uid") + ".jpg").getDownloadUrl()
                                                                   .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                       @Override
                                                                       public void onSuccess(Uri uri) {
                                                                           // Got the download URL for 'profilepics/uid.jpg'
                                                                           Log.d("control", "sucess");
                                                                           users.add(new User(
                                                                                   document.getString("uid"),
                                                                                   document.getString("userName"),
                                                                                   uri));
                                                                           actualizarAdapter(users);

                                                                       }
                                                                   })
                                                                   .addOnFailureListener(new OnFailureListener() {
                                                                       @Override
                                                                       public void onFailure(@NonNull Exception exception) {
                                                                           // File not found
                                                                           Log.d("control", "fail");
                                                                           users.add(new User(
                                                                                   document.getString("uid"),
                                                                                   document.getString("userName"),
                                                                                   null));
                                                                           actualizarAdapter(users);
                                                                       }
                                                                   });
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
                Intent intent = new Intent(getApplicationContext(), PersonProfileActivity.class);
                intent.putExtra("name", u.getName());
                intent.putExtra("uid", u.getUid());
                intent.putExtra("uri", String.valueOf(u.getUri()));
                startActivity(intent);
            }
        }, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

}
