package com.santiotin.nite.Activities;


import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.santiotin.nite.Holders.AllEventHolder;
import com.santiotin.nite.Holders.EventHolder;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Parsers.SnapshotParserEvent;
import com.santiotin.nite.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PruebasActivity extends AppCompatActivity {

    private TextView dateTextView;
    private FirestoreRecyclerAdapter fbAdapter;
    private FirestoreRecyclerAdapter fbAdapter1;

    private int actualYear;
    private int actualMonth;
    private int actualDay;

    private TextView tvNoResults;
    private TextView tvError;
    private ImageView imgViewNoResults;
    private ProgressBar progressBar;

    private static final int BCN_CODE = 0;
    private static final int MAD_CODE = 1;

    private int cityCode;
    private Button btnCity;

    private RecyclerView recyclerViewRecommend;
    private RecyclerView recyclerViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pruebas);

        recyclerViewRecommend = findViewById(R.id.recyclerViewRecommend);
        recyclerViewAll = findViewById(R.id.recyclerViewAll);

        progressBar = findViewById(R.id.progresBarTodayRecomend );
        tvNoResults = findViewById(R.id.tvTodayNoResultsRecomend );
        tvError = findViewById(R.id.tvTodayErrorRecomend );
        imgViewNoResults = findViewById(R.id.imgViewNoResultsRecomend );


        iniCityCode();
        iniToolbar();
        iniRecyclerViewAll();
        iniRecyclerViewRecomend();
        iniDatePicker();
        iniDate();
        iniMap();

    }

    public void iniCityCode(){
        cityCode = BCN_CODE;
    }

    public  void iniToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void iniRecyclerViewAll(){
        recyclerViewRecommend = findViewById(R.id.recyclerViewAll);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PruebasActivity.this, LinearLayoutManager.HORIZONTAL, false);

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        recyclerViewRecommend.setHasFixedSize(true);

        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        recyclerViewRecommend.setItemAnimator(new DefaultItemAnimator());

        // Enlazamos el layout manager y adaptador directamente al recycler view
        recyclerViewRecommend.setLayoutManager(mLayoutManager);
    }

    public void iniRecyclerViewRecomend(){
        recyclerViewRecommend = findViewById(R.id.recyclerViewRecommend);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PruebasActivity.this, LinearLayoutManager.HORIZONTAL, false);

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        recyclerViewRecommend.setHasFixedSize(true);

        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        recyclerViewRecommend.setItemAnimator(new DefaultItemAnimator());

        // Enlazamos el layout manager y adaptador directamente al recycler view
        recyclerViewRecommend.setLayoutManager(mLayoutManager);
    }

    public void iniDatePicker(){
        dateTextView = (TextView) findViewById(R.id.dateTextView);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_MONTH, actualDay);
                c.set(Calendar.MONTH, actualMonth);
                c.set(Calendar.YEAR, actualYear);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(PruebasActivity.this, R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // +1 because january is zero
                        displayDate(year,month,day);

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.today), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar c = Calendar.getInstance();
                        displayDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
                    }
                });
                datePickerDialog.show();

            }
        });
    }

    public void iniDate(){
        Calendar c = Calendar.getInstance();
        actualYear = c.get(Calendar.YEAR);
        actualMonth = c.get(Calendar.MONTH);
        actualDay = c.get(Calendar.DAY_OF_MONTH);

        displayDate(actualYear,actualMonth,actualDay);
    }

    public void iniMap(){
        ImageButton mapBtn = findViewById(R.id.btnMapa);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PruebasActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
    }

    public void displayDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        actualDay = day;
        actualMonth = month;
        actualYear = year;

        SimpleDateFormat m = new SimpleDateFormat("MMM");
        SimpleDateFormat w = new SimpleDateFormat("E");
        SimpleDateFormat d = new SimpleDateFormat("d");
        SimpleDateFormat y = new SimpleDateFormat("y");

        String smonth = m.format(c.getTime());
        String sweek = w.format(c.getTime());
        String sday = d.format(c.getTime());
        String syear = y.format(c.getTime());

        String date = sweek + ' ' + sday + ' ' + smonth + " " + syear;
        dateTextView.setText(date);

        getEventsOfDay(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        getEventsOfDayRecommend(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        fbAdapter.startListening();

    }

    public void getEventsOfDay(final int year, final int month, final int day){

        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.INVISIBLE);
        imgViewNoResults.setVisibility(View.INVISIBLE);

        String cityName = codeToCity(cityCode);

        Query query = FirebaseFirestore.getInstance()
                .collection("events")
                .whereEqualTo("day", day)
                .whereEqualTo("month", month+1)
                .whereEqualTo("year", year)
                .whereEqualTo("city", cityName)
                .orderBy("priority", Query.Direction.DESCENDING)
                .orderBy("club");

        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, new SnapshotParserEvent())
                .build();

        fbAdapter = new FirestoreRecyclerAdapter<Event, AllEventHolder>(options) {
            @Override
            public AllEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_event2, parent, false);

                return new AllEventHolder(view);
            }


            @Override
            protected void onBindViewHolder(AllEventHolder holder, final int position, final Event e) {
                holder.setTitle(e.getName());
                holder.setClub(e.getClub());
                holder.setNumAssists(e.getNumAssistants());
                holder.setFondo(PruebasActivity.this, e.getId());

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PruebasActivity.this, EventDescriptionActivity.class);
                        intent.putExtra("event", e);
                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(PruebasActivity.this, v.findViewById(R.id.cardView), "imageEvent");
                        startActivity(intent, options.toBundle());
                    }
                });

                holder.btnFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PruebasActivity.this, AssistantsFriendsActivity.class);
                        intent.putExtra("event", e);
                        startActivity(intent);
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
                    imgViewNoResults.setVisibility(View.INVISIBLE);

                }else{
                    Log.d("control", "si que hay");
                    tvNoResults.setVisibility(View.VISIBLE);
                    imgViewNoResults.setVisibility(View.VISIBLE);
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

        recyclerViewAll.setAdapter(fbAdapter);
        fbAdapter.startListening();
    }

    public void getEventsOfDayRecommend(final int year, final int month, final int day){

        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.INVISIBLE);
        imgViewNoResults.setVisibility(View.INVISIBLE);

        String cityName = codeToCity(cityCode);

        Query query = FirebaseFirestore.getInstance()
                .collection("events")
                .whereEqualTo("day", day)
                .whereEqualTo("month", month+1)
                .whereEqualTo("year", year)
                .whereEqualTo("city", cityName)
                .orderBy("priority", Query.Direction.DESCENDING)
                .orderBy("club");

        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, new SnapshotParserEvent())
                .build();

        fbAdapter1 = new FirestoreRecyclerAdapter<Event, EventHolder>(options) {
            @Override
            public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_event_recommend, parent, false);

                return new EventHolder(view);
            }


            @Override
            protected void onBindViewHolder(EventHolder holder, final int position, final Event e) {
                holder.setTitle(e.getClub() + ": " + e.getName());
                holder.setNumAssists(e.getNumAssistants());
                holder.setFondo(PruebasActivity.this, e.getId());

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PruebasActivity.this, EventDescriptionActivity.class);
                        intent.putExtra("event", e);
                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(PruebasActivity.this, v.findViewById(R.id.cardView), "imageEvent");
                        startActivity(intent, options.toBundle());
                    }
                });

                holder.btnFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PruebasActivity.this, AssistantsFriendsActivity.class);
                        intent.putExtra("event", e);
                        startActivity(intent);
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
                    imgViewNoResults.setVisibility(View.INVISIBLE);

                }else{
                    Log.d("control", "si que hay");
                    tvNoResults.setVisibility(View.VISIBLE);
                    imgViewNoResults.setVisibility(View.VISIBLE);
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

        recyclerViewRecommend.setAdapter(fbAdapter1);
        fbAdapter1.startListening();
    }


    private String codeToCity(int code){
        switch (code) {
            case BCN_CODE:
                return getString(R.string.barcelona);
            case MAD_CODE:
                return getString(R.string.madrid);
            default:
                return getString(R.string.barcelona);
        }
    }

    private String codeToInicialesDeCity(int code){
        switch (code) {
            case BCN_CODE:
                return getString(R.string.bcn);
            case MAD_CODE:
                return getString(R.string.mad);
            default:
                return getString(R.string.bcn);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fbAdapter.startListening();
        fbAdapter1.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        fbAdapter.stopListening();
        fbAdapter1.startListening();
    }

}
