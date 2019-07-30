package com.santiotin.nite;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.santiotin.nite.Holders.UserHolder;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Models.User;

public class AssistantsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Event event;
    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter fbAdapter;

    private TextView tvNoResults;
    private ImageView imgViewNoResults;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistants);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        event = (Event) getIntent().getSerializableExtra("event");

        progressBar = findViewById(R.id.progressBarAssistants);
        tvNoResults = findViewById(R.id.tvNoResultsAssistants);
        imgViewNoResults = findViewById(R.id.imgViewNoResultsAssists);


        iniToolbar();
        iniRecyclerView();
        getAssistantsOfEvent();



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
        getSupportActionBar().setTitle(getString(R.string.assistants));
    }

    public void iniRecyclerView(){

        mRecyclerView = findViewById(R.id.recyclerViewAssists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);

        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);


    }

    public void getAssistantsOfEvent(){

        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.INVISIBLE);
        imgViewNoResults.setVisibility(View.INVISIBLE);

        Query query = FirebaseFirestore.getInstance()
                .collection("events")
                .document(event.getId())
                .collection("assistingUsers");

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, new SnapshotParser<User>() {
                    @NonNull
                    @Override
                    public User parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        User u = new User(snapshot.getId(),
                                snapshot.getString("userName"));
                        return u;
                    }
                })
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
                Long photoTime = System.currentTimeMillis() / (1000*60*60);
                holder.setName(u.getName());
                holder.setImage(getApplicationContext(), u.getUid(), photoTime);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!u.getUid().equals(user.getUid())){
                            Intent intent = new Intent(getApplicationContext(), PersonProfileActivity.class);
                            intent.putExtra("user", u);
                            startActivity(intent);
                        }
                    }
                });

            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                progressBar.setVisibility(View.INVISIBLE);
                if (getItemCount() > 0){
                    Log.d("control", "no hay na");
                    tvNoResults.setVisibility(View.INVISIBLE);
                    imgViewNoResults.setVisibility(View.INVISIBLE);

                }else{
                    Log.d("control", "si que hay");
                    tvNoResults.setText(getString(R.string.no_assists));
                    tvNoResults.setVisibility(View.VISIBLE);
                    imgViewNoResults.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                progressBar.setVisibility(View.INVISIBLE);
                tvNoResults.setText(getString(R.string.error));
                tvNoResults.setVisibility(View.VISIBLE);
                imgViewNoResults.setVisibility(View.GONE);
            }

        };

        mRecyclerView.setAdapter(fbAdapter);
        fbAdapter.startListening();
    }


    @Override
    protected void onStart() {
        super.onStart();
        fbAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fbAdapter.stopListening();
    }
}
