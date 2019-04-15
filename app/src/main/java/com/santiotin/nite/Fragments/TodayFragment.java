package com.santiotin.nite.Fragments;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.santiotin.nite.Adapters.RVCardListAdp;
import com.santiotin.nite.AssistantsActivity;
import com.santiotin.nite.EditProfileActivity;
import com.santiotin.nite.EventDescriptionActivity;
import com.santiotin.nite.LoginActivity;
import com.santiotin.nite.MainActivity;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.R;
import java.io.Serializable;
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

    public TodayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_today, container, false);

        //inicializar toolbar
        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        //declarar textofecha y ponerle fecha
        final TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        displayDate(dateTextView, 0,0,0, false);

        //llenar recyclerRiew con eventos
        llenarRecyclerView(view);

        //inicializar swipe refresh layout
        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        //inicializar boton de mapa
        ImageButton mapBtn = view.findViewById(R.id.btnMapa);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // para pruebas
            }
        });

        //inicializar datepicker
        ImageButton btnCal = view.findViewById(R.id.btnDate);
        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // +1 because january is zero

                        displayDate(dateTextView,year,month,day,true);

                    }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Hoy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        displayDate(dateTextView, 0,0,0, false);
                    }
                });
                datePickerDialog.show();

            }
        });
        btnCal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                displayDate(dateTextView, 0,0,0, false);
                return true;
            }
        });

        return view;
    }

    public void displayDate(TextView tv, int year, int month, int day, Boolean b) {
        Calendar c = Calendar.getInstance();
        if (b) {
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);

        }

        SimpleDateFormat m = new SimpleDateFormat("MMM");
        SimpleDateFormat w = new SimpleDateFormat("E");
        SimpleDateFormat d = new SimpleDateFormat("d");
        SimpleDateFormat y = new SimpleDateFormat("y");

        String smonth = m.format(c.getTime());
        String sweek = w.format(c.getTime());
        String sday = d.format(c.getTime());
        String syear = y.format(c.getTime());

        String date = sweek + ' ' + sday + ' ' + smonth + " " + syear;
        tv.setText(date);

    }

    public void llenarRecyclerView(final View v) {

        Date date = Calendar.getInstance().getTime();

        final List<Event> events = new ArrayList<>();
        events.add(new Event("Lolita", "Sutton", "Carrer de tusset, 13", getString(R.string.lorem_ipsum), date, 10, 6, 12, 693, R.drawable.event_sutton));
        events.add(new Event("Pacha chá", "Pacha", "Ramón Trias Fargas, 2", getString(R.string.lorem_ipsum), date, 12, 5, 15, 748, R.drawable.event_pacha));
        events.add(new Event("No Rules", "Otto Zutz", "Carrer de Lincoln, 15", getString(R.string.lorem_ipsum), date, 10, 4, 8, 316, R.drawable.event_otto));
        events.add(new Event("Obsession", "Bling Bling", "Carrer de tusset, 8", getString(R.string.lorem_ipsum), date, 11, 6, 10, 882, R.drawable.event_bling));
        events.add(new Event("Lolita", "Sutton", "Carrer de tusset, 13", getString(R.string.lorem_ipsum), date, 10, 6, 12, 693, R.drawable.event_sutton));
        events.add(new Event("Pacha chá", "Pacha", "Ramón Trias Fargas, 2", getString(R.string.lorem_ipsum), date, 10, 6, 15, 748, R.drawable.event_pacha));
        events.add(new Event("No Rules", "Otto Zutz", "Carrer de Lincoln, 15", getString(R.string.lorem_ipsum), date, 10, 6, 8, 316, R.drawable.event_otto));
        events.add(new Event("Obsession", "Bling Bling", "Carrer de tusset, 8", getString(R.string.lorem_ipsum), date, 10, 6, 12, 882, R.drawable.event_bling));
        events.add(new Event("Lolita", "Sutton", "Carrer de tusset, 13", getString(R.string.lorem_ipsum), date, 10, 6, 12, 693, R.drawable.event_sutton));
        events.add(new Event("Pacha chá", "Pacha", "Ramón Trias Fargas, 2", getString(R.string.lorem_ipsum), date, 10, 6, 15, 748, R.drawable.event_pacha));
        events.add(new Event("No Rules", "Otto Zutz", "Carrer de Lincoln, 15", getString(R.string.lorem_ipsum), date, 10, 6, 8, 316, R.drawable.event_otto));
        events.add(new Event("Obsession", "Bling Bling", "Carrer de tusset, 8", getString(R.string.lorem_ipsum), date, 10, 6, 12, 882, R.drawable.event_bling));

        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // Implementamos nuestro OnClickListener propio, sobrescribiendo el metodo que nosotros
        // definimos en el adaptador, y recibiendo los parámetros que necesitamos


        RecyclerView.Adapter mAdapter = new RVCardListAdp(events, R.layout.item_event, new RVCardListAdp.OnItemClickListener() {
            @Override
            public void onItemClick(Event e, int position) {
                Intent intent = new Intent(getContext(), EventDescriptionActivity.class);
                intent.putExtra("event", e);
                startActivity(intent);
            }

            @Override
            public void onFriendsClick(Event e, int position) {

                List<User> users = new ArrayList<>();
                users.add(new User("Amigo1", R.drawable.event_sutton));
                users.add(new User("Amigo2", R.drawable.event_pacha));
                users.add(new User("Amigo3", R.drawable.event_otto));
                users.add(new User("Amigo4", R.drawable.event_bling));
                users.add(new User("Amigo5", R.drawable.event_sutton));
                users.add(new User("Amigo6", R.drawable.event_pacha));
                users.add(new User("Amigo7", R.drawable.event_otto));
                users.add(new User("Amigo8", R.drawable.event_bling));

                Intent intent = new Intent(getContext(), AssistantsActivity.class);
                intent.putExtra("Friends", (Serializable) users);
                startActivity(intent);

            }
        });
        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);
        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

}
