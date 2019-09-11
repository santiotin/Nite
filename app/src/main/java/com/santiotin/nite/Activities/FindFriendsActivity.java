package com.santiotin.nite.Activities;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.santiotin.nite.Adapters.RVFindFriendsSmallAdapter;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.Parsers.SnapshotParserUser;
import com.santiotin.nite.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class FindFriendsActivity extends AppCompatActivity {

    private static final int CONTACTS_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser fbUser;
    private User mUser;

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private List<User> users;

    private TextView tvNoResults;
    private TextView tvWait;
    private ImageView imgViewNoResults;
    private RelativeLayout continuar;
    private String control;

    private int x = 0;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fbUser = mAuth.getCurrentUser();

        mUser = null;
        users = new ArrayList<>();

        progressBar = findViewById(R.id.progressBarFindFriends);
        tvNoResults = findViewById(R.id.tvNoResultsFindFriends);
        tvWait = findViewById(R.id.tvWaitFindFriends);
        imgViewNoResults = findViewById(R.id.imgViewNoResultsFindFriends);


        continuar = findViewById(R.id.continuarButton);
        continuar.setVisibility(View.INVISIBLE);
        control = (String) getIntent().getSerializableExtra("control");

        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.VISIBLE);
        imgViewNoResults.setVisibility(View.INVISIBLE);
        tvWait.setVisibility(View.INVISIBLE);

        iniToolbar();
        iniRecyclerView();
        listenUser();


        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindFriendsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }


    private void iniToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbarFindFriends);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.friends));

    }

    public void iniRecyclerView() {

        mRecyclerView = findViewById(R.id.recyclerViewFindFriends);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);
        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);


    }

    private void listenUser() {
        db.collection("users")
                .document(fbUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Log.d("control", "Current data: " + documentSnapshot.getData());
                            SnapshotParserUser spu = new SnapshotParserUser();
                            mUser = spu.parseSnapshot(documentSnapshot);
                            permisosContactos();

                        } else {
                            Log.d("control", "Current data: null");
                        }
                    }
                });
    }

    private void permisosContactos() {

        //Checking if the user has granted contacts permission for this app
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS
            }, CONTACTS_REQUEST_CODE);
            permisosContactos();

        } else {
            getAllFriends();
        }
    }

    private void getAllFriends() {

        progressBar.setVisibility(View.VISIBLE);
        tvWait.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.INVISIBLE);
        imgViewNoResults.setVisibility(View.INVISIBLE);

        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL", null, null);
        while (cursor.moveToNext()) {
            String c = cursor.getString(0);
            //System.out.println("telefono:" + c) ;

            if (!c.equals(mUser.getPhone()) || c.equals("+34" + mUser.getPhone())) {
                x++;
                Log.d("control", "numerooo: " + x);
                getFriend(c);
            }
        }
        total = x;
        progressBar.setMax(total);
    }

    public void getFriend(String c) {
        db.collection("users")
                .whereArrayContains("findPhones", c)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        --x;
                        Log.d("control", "numerooo: " + x);
                        for (final QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SnapshotParserUser spu = new SnapshotParserUser();
                            users.add(spu.parseSnapshot(documentSnapshot));
                        }
                        actualizarAdapter(users);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        --x;
                        Log.d("control", "numerooo: " + x);
                        actualizarAdapter(users);
                    }
                });
    }

    public void actualizarAdapter(List<User> users) {
        Log.d("control", "numero: " + x);
        if (x != 0){
            progressBar.setProgress(total - x);
        }
        else{
            final RecyclerView.Adapter mAdapter = new RVFindFriendsSmallAdapter(users, R.layout.item_friend_follow, new RVFindFriendsSmallAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(User u, int position) {
                    if (!u.getUid().equals(fbUser.getUid())) {
                        Intent intent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
                        intent.putExtra("user", u);
                        startActivity(intent);
                    }
                }

            }, FindFriendsActivity.this);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("control", "Asi esperamos a que se carguen las relaciones");
                }
            }, 1000);

            if (!users.isEmpty()) {
                progressBar.setVisibility(View.INVISIBLE);
                tvWait.setVisibility(View.INVISIBLE);
                tvNoResults.setVisibility(View.INVISIBLE);
                imgViewNoResults.setVisibility(View.INVISIBLE);
                Log.d("control", "no esta vacio");
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                tvWait.setVisibility(View.INVISIBLE);
                tvNoResults.setVisibility(View.VISIBLE);
                imgViewNoResults.setVisibility(View.VISIBLE);
                Log.d("control", "esta vacio");
            }

            if (control.equals("0")) {
                continuar.setVisibility(View.VISIBLE);
            }
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarAdapter(users);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        finish();
        return true;
    }
}
