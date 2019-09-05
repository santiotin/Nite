package com.santiotin.nite;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiotin.nite.Adapters.RVFindFriendsSmallAdapter;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.Parsers.SnapshotParserUser;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class FindFriendsActivity extends AppCompatActivity {

    private static final int CONTACTS_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private List<User> users;


    private TextView tvNoResults;
    private ImageView imgViewNoResults;

    private User mUser;

    private RelativeLayout continuar;

    private String control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        mUser = null;

        progressBar = findViewById(R.id.progressBarFindFriends);
        tvNoResults = findViewById(R.id.tvNoResultsFindFriends);
        imgViewNoResults = findViewById(R.id.imgViewNoResultsFindFriends);
        users = new ArrayList<>();

        continuar = findViewById(R.id.continuarButton);
        continuar.setVisibility(View.INVISIBLE);

        control = (String)getIntent().getSerializableExtra("control");

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

    public void getUserFriends(String c){

        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.INVISIBLE);
        imgViewNoResults.setVisibility(View.INVISIBLE);

        db.collection("users").whereArrayContains("findPhones", c)
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

                                                           if (document.exists()) {
                                                               users.add(new User(
                                                                       document.getId(),
                                                                       document.getString("name")));
                                                           } else {
                                                               Log.d("control", "No such document");
                                                           }
                                                       }
                                                   }

                                               } else {
                                                   Log.d("control", "Error getting documents: ", task.getException());
                                               }
                                           }
                                       }
                );
    }




    private void iniToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbarFindFriends);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.friends));

    }

    public void actualizarAdapter(List<User> users){
        if (!users.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            tvNoResults.setVisibility(View.INVISIBLE);
            imgViewNoResults.setVisibility(View.INVISIBLE);
        }else{
            //De momento nada, habrá que poner imagen o algo
        }
        RecyclerView.Adapter mAdapter = new RVFindFriendsSmallAdapter(users, R.layout.item_friend_follow, new RVFindFriendsSmallAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User u, int position) {
                if (!u.getUid().equals(currentUser.getUid())) {
                    Intent intent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
                    intent.putExtra("user", u);
                    startActivity(intent);
                }
            }

        },FindFriendsActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        if (control.equals("0")){

            continuar.setVisibility(View.VISIBLE);
        }


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


    private void permisosContactos() {

        //Checking if the user has granted contacts permission for this app
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS
            }, CONTACTS_REQUEST_CODE);

        } else {

            obtenerDatos();
        }
    }

    private void obtenerDatos() {

        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL", null, null);
        while (cursor.moveToNext()) {
            String c = cursor.getString(0);
            //System.out.println("telefono:" + c) ;

            if (!c.equals(mUser.getPhone()) || c.equals("+34"+mUser.getPhone())){

                getUserFriends(c);
            }
        }
        actualizarAdapter(users);


    }


    private void listenUser() {
        final DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("control", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("control", "Current data: " + snapshot.getData());
                    SnapshotParserUser spu = new SnapshotParserUser();
                    mUser = spu.parseSnapshot(snapshot);
                    iniToolbar();
                    iniRecyclerView();
                    permisosContactos();

                } else {
                    Log.d("control", "Current data: null");
                }
            }
        });
    }


}
