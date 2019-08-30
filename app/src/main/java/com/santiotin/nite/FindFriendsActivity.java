package com.santiotin.nite;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiotin.nite.Adapters.RVFriendsSmallAdapter;
import com.santiotin.nite.Holders.UserHolder;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.Parsers.SnapshotParserUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import io.opencensus.tags.Tag;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class FindFriendsActivity extends AppCompatActivity {

    private static final int CONTACTS_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private List<User> users;


    private TextView tvNoResults;
    private ImageView imgViewNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        progressBar = findViewById(R.id.progressBarFindFriends);
        tvNoResults = findViewById(R.id.tvNoResultsFindFriends);
        imgViewNoResults = findViewById(R.id.imgViewNoResultsFindFriends);
        users = new ArrayList<>();

        iniToolbar();
        iniRecyclerView();
        permisosContactos();


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
                                                               actualizarAdapter(users);
                                                               Log.d("control", "DocumentSnapshot data: " + document.getData());
                                                           } else {
                                                               actualizarAdapter(users);
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
        RecyclerView.Adapter mAdapter = new RVFriendsSmallAdapter(users, R.layout.item_friend, new RVFriendsSmallAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User u, int position) {
                if (!u.getUid().equals(user.getUid())){
                    Intent intent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
                    intent.putExtra("user", u);
                    startActivity(intent);
                }
            }
        }, FindFriendsActivity.this);
        mRecyclerView.setAdapter(mAdapter);
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
            System.out.println("telefono:" + c) ;
            getUserFriends(c);
        }


    }
}
