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
import android.widget.CalendarView;
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
import com.santiotin.nite.Holders.MyEventHolder;
import com.santiotin.nite.Models.Event;

import java.util.Calendar;

public class MyEventsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private FirestoreRecyclerAdapter fbAdapter;
    private TextView tvNoResults;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        progressBar = findViewById(R.id.progresBarMyEvents);
        tvNoResults = findViewById(R.id.tvMyEventsNoResults);
        tvError = findViewById(R.id.tvMyEventsError);

        CalendarView calendarView = findViewById(R.id.calendarMyEvents);


        iniToolbar();
        iniRecyclerView();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                getUserEvents(dayOfMonth, month + 1, year);
            }
        });

        Calendar cal = Calendar.getInstance();

        getUserEvents(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));

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

    public void getUserEvents(int day, int month, int year){

        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.INVISIBLE);

        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("assistingEvents")
                .whereEqualTo("eventDay", day)
                .whereEqualTo("eventMonth", month)
                .whereEqualTo("eventYear", year);

        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, new SnapshotParser<Event>() {
                    @NonNull
                    @Override
                    public Event parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Event nou = new Event(snapshot.getId(),
                                snapshot.getString("eventName"),
                                snapshot.getString("eventClub"),
                                snapshot.getBoolean("eventList"));
                        return nou;
                    }
                })
                .build();

        fbAdapter = new FirestoreRecyclerAdapter<Event, MyEventHolder>(options) {
            @Override
            public MyEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_event_ticket, parent, false);

                return new MyEventHolder(view);
            }


            @Override
            protected void onBindViewHolder(MyEventHolder holder, final int position, final Event e) {
                holder.setTitleAndClub(e.getName(), e.getClub());
                holder.setFondo(getApplicationContext(), e.getId());

                holder.rlSeeEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), EventDescriptionActivity.class);
                        intent.putExtra("event", e);
                        intent.putExtra("notComplete", true);
                        startActivity(intent);
                    }
                });

                if (e.hasLists() != null && e.hasLists())holder.imgBtnList.setColorFilter(getResources().getColor(R.color.pink2));
                else holder.imgBtnList.setColorFilter(getResources().getColor(R.color.grey));
                holder.imgBtnList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (e.hasLists()){
                            Intent intent = new Intent(getApplicationContext(), QRCodeTicket.class);
                            intent.putExtra("event", e);
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                progressBar.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.INVISIBLE);
                if (getItemCount() > 0){
                    Log.d("control", "no hay na");
                    tvNoResults.setVisibility(View.INVISIBLE);

                }else{
                    Log.d("control", "si que hay");
                    tvNoResults.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                progressBar.setVisibility(View.INVISIBLE);
                tvNoResults.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);


            }
        };



        mRecyclerView.setAdapter(fbAdapter);
        fbAdapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fbAdapter != null)fbAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fbAdapter != null) fbAdapter.stopListening();
    }
}
