package com.santiotin.nite.Fragments;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiotin.nite.Adapters.RVCardListAdp;
import com.santiotin.nite.EventDescriptionActivity;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.PruebasActivity;
import com.santiotin.nite.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private View view;
    private RecyclerView mRecyclerView;
    private TextView dateTextView;
    private ProgressBar progressBar;
    private int actualYear;
    private int actualMonth;
    private int actualDay;

    public TodayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_today, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //declaracion de elementos
        dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        ImageButton mapBtn = view.findViewById(R.id.btnMapa);
        progressBar = view.findViewById(R.id.progresBarToday);

        //inicializar Recyclerview y toolbar
        iniRecyclerView();
        iniToolbar();
        iniDate();



        //inicializar swipe refresh layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        displayDate(actualYear,actualMonth,actualDay);
                        swipeRefreshLayout.setRefreshing(false);


                    }
                }, 0);
            }
        });

        //inicializar boton de mapa
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PruebasActivity.class);
                startActivity(i);
            }
        });

        //inicializar datepicker
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_MONTH, actualDay);
                c.set(Calendar.MONTH, actualMonth);
                c.set(Calendar.YEAR, actualYear);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // +1 because january is zero

                        displayDate(year,month,day);

                    }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Hoy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar c = Calendar.getInstance();
                        displayDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
                    }
                });
                datePickerDialog.show();

            }
        });


        return view;
    }

    public void displayDate(int year, int month, int day) {
        //vacio recyclerview
        final List<Event> events = new ArrayList<>();
        actualizarAdapter(events);

        progressBar.setVisibility(View.VISIBLE);

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

        llenarRecyclerView(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

    }

    public void llenarRecyclerView(int year, int month, int day) {

        final List<Event> events = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        final Date date = c.getTime();

        db.collection("events")
                .whereEqualTo("year", year)
                .whereEqualTo("month", month+1)
                .whereEqualTo("day", day)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                actualizarAdapter(events);
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Event nou = new Event(document.getId(),
                                        document.getString("name"),
                                        document.getString("club"),
                                        document.getString("addr"),
                                        document.getString("descr"),
                                        date,
                                        Integer.valueOf(document.getString("starthour")),
                                        Integer.valueOf(document.getString("endhour")),
                                        12,
                                        Integer.valueOf(document.getString("assists")),
                                        R.drawable.event_sutton);
                                events.add(nou);
                                actualizarAdapter(events);
                            }
                        } else {
                            Log.d("control", "Error getting documents: ", task.getException());
                            actualizarAdapter(events);
                        }
                    }
                }
        );
    }

    public  void iniToolbar(){
        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void iniRecyclerView(){
        mRecyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);
        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void iniDate(){
        Calendar c = Calendar.getInstance();
        actualYear = c.get(Calendar.YEAR);
        actualMonth = c.get(Calendar.MONTH);
        actualDay = c.get(Calendar.DAY_OF_MONTH);

        displayDate(actualYear,actualMonth,actualDay);
    }

    public void actualizarAdapter(List<Event> listEvents){
        RecyclerView.Adapter mAdapter = new RVCardListAdp(listEvents, R.layout.item_event, new RVCardListAdp.OnItemClickListener() {
            @Override
            public void onItemClick(Event e, int position) {
                Intent intent = new Intent(getContext(), EventDescriptionActivity.class);
                intent.putExtra("event", e);
                startActivity(intent);
            }

            @Override
            public void onFriendsClick(Event e, int position) {

                /*Intent intent = new Intent(getContext(), AssistantsActivity.class);
                intent.putExtra("event", e);
                startActivity(intent);*/

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

}
