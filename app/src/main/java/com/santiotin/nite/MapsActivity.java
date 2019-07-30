package com.santiotin.nite;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.EventMapAdapter;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Parsers.SnapshotParserEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Event> events;
    private FirebaseFirestore db;
    private SupportMapFragment mapFragment;
    ViewPager viewPager;
    EventMapAdapter adapter;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    static public final int LOCATION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = FirebaseFirestore.getInstance();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        iniToolbar();
        getEventsOfDay();



    }

    public void iniToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.map));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        for (int i = 0; i < events.size(); i++){
            final int tag = i;
            final Event event = events.get(i);
            final LatLng ltg = new LatLng(event.getLati(), event.getLongi());
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + event.getId() + ".jpg");
            GlideApp.with(MapsActivity.this)
                    .asBitmap()
                    .load(storageRef)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Marker m = mMap.addMarker(new MarkerOptions().position(ltg).title(event.getClub()));
                            m.setTag(tag);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                            Bitmap b = Bitmap.createScaledBitmap(resource, 150, 150, true);
                            Marker m = mMap.addMarker(new MarkerOptions()
                                    .position(ltg)
                                    .title(event.getClub())
                                    .icon(BitmapDescriptorFactory.fromBitmap(b))
                                    .snippet(event.getName()));
                            m.setTag(tag);
                            return false;
                        }
                    })
                    .centerInside()
                    .circleCrop()
                    .preload();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                marker.getId();
                viewPager.setCurrentItem((int)marker.getTag(),true);
                return true;
            }
        });

        iniAdapter();
        checkLocation();

    }

    private void checkLocation(){
        //Checking if the user has granted location permission for this app
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
    /*
    Requesting the Location permission
    1st Param - Activity
    2nd Param - String Array of permissions requested
    3rd Param -Unique NotRequest code. Used to identify these set of requested permission
    */
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);
        }else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng ltgaux = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltgaux, 15.0f));
                }
            });

        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    //Permission Granted
                    checkLocation();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
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
                .orderBy("priority", Query.Direction.DESCENDING)
                .orderBy("club")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("control", "recibo eventos mapa");
                        for (final QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("control", "Recibo Evento");
                            SnapshotParserEvent spe = new SnapshotParserEvent();
                            Event aux = spe.parseSnapshot(document);
                            events.add(aux);
                        }
                        iniMap();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("control", "error mapa", e.getCause());
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
