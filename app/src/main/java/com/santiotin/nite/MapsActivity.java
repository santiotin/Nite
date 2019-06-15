package com.santiotin.nite;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiotin.nite.Adapters.EventMapAdapter;
import com.santiotin.nite.Models.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Event> events;
    private FirebaseFirestore db;
    private SupportMapFragment mapFragment;
    ViewPager viewPager;
    EventMapAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = FirebaseFirestore.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        iniToolbar();
        getEventsOfDay();



    }

    public void iniToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boolean first = false;

        // Add a marker in Sydney and move the camera
        for (Event e : events){
            LatLng ltg = new LatLng(e.getLati(), e.getLongi());
            mMap.addMarker(new MarkerOptions().position(ltg).title(e.getClub()));
            if (!first){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltg, 15.0f));
                first = true;
            }
        }

        iniAdapter();

    }

    private void getEventsOfDay(){
        events = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        db.collection("events")
                .whereEqualTo("day", day)
                .whereEqualTo("month", month+1)
                .whereEqualTo("year", year)
                .orderBy("club")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("control", "entro");
                        for (final QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("control", "Recibo Evento");
                            SnapshotParserEvent spe = new SnapshotParserEvent();
                            Event aux = spe.parseSnapshot(document);
                            events.add(aux);
                        }
                        iniMap();
                    }
                });
    }

    private void iniMap(){
        mapFragment.getMapAsync(this);
    }

    private void iniAdapter(){
        adapter = new EventMapAdapter(events, this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        int w = viewPager.getWidth();
        viewPager.setPadding(0,0, (int)(w * 0.6),0);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                LatLng ltg = new LatLng(events.get(i).getLati(), events.get(i).getLongi());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltg, 15.0f));
            }

            @Override
            public void onPageSelected(int i) {
                
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


}
