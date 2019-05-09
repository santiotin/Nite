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
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.RVCardListAdp;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        progressBar = findViewById(R.id.progresBarMyEvents);
        progressBar.setVisibility(View.INVISIBLE);

        storageRef = FirebaseStorage.getInstance().getReference();

        iniToolbar();
        iniRecyclerView();
        getUserEvents();

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
        getSupportActionBar().setTitle(getString(R.string.myEvents));
    }

    public void iniRecyclerView(){
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView = findViewById(R.id.recyclerViewMyEvents);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);
        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void getUserEvents(){
        final List<Event> events = new ArrayList<>();

        db.collection("assistants")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   if (task.getResult().isEmpty()) {
                                                       actualizarAdapter(events);
                                                   }
                                                   for (final QueryDocumentSnapshot document : task.getResult()) {
                                                       storageRef.child("eventpics/" + document.getId()).getDownloadUrl()
                                                               .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                   @Override
                                                                   public void onSuccess(Uri uri) {
                                                                       // Got the download URL for 'profilepics/uid.jpg'

                                                                       Event nou = new Event(document.getString("eid"),
                                                                               document.getString("eventName"),
                                                                               document.getString("club"),
                                                                               uri.toString());
                                                                       events.add(nou);
                                                                       actualizarAdapter(events);

                                                                   }
                                                               });
                                                   }
                                               } else {
                                                   Log.d("control", "Error getting documents: ", task.getException());
                                                   actualizarAdapter(events);
                                               }
                                           }
                                       }
                );
    }

    public void actualizarAdapter(List<Event> listEvents){
        RecyclerView.Adapter mAdapter = new RVCardListAdp(listEvents, R.layout.item_event_sin_participantes, new RVCardListAdp.OnItemClickListener() {
            @Override
            public void onItemClick(Event e, int position) {
                //llamar al evento antes de iniciar la activity
                Intent intent = new Intent(getApplicationContext(), EventDescriptionActivity.class);
                intent.putExtra("event", e);
                startActivity(intent);
            }

            @Override
            public void onFriendsClick(Event e, int position) {

                Intent intent = new Intent(getApplicationContext(), AssistantsActivity.class);
                intent.putExtra("event", e);
                startActivity(intent);

            }
        }, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

}
