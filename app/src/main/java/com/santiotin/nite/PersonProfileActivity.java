package com.santiotin.nite;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private String uidFriend;
    private String nameFriend;
    private Uri uriFriend;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    Boolean meSigue;
    Boolean leSigo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        uidFriend = getIntent().getStringExtra("uid");
        nameFriend = getIntent().getStringExtra("name");
        uriFriend = Uri.parse(getIntent().getStringExtra("uri"));





        iniToolbar();
        iniCampos();
        consultarRelacion();
    }

    public void iniToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return true;
    }

    private void iniCampos(){

        TextView tvname = findViewById(R.id.personName);
        CircularImageView image = findViewById(R.id.imgViewCirclePerson);
        tvname.setText(nameFriend);

        if (String.valueOf(uriFriend).equals("null")){
            image.setImageResource(R.drawable.logo);
            image.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        }
        else{
            Glide.with(getApplicationContext())
                    .load(uriFriend)
                    .into(image);
        }



    }

    private void consultarRelacion(){
        meSigue = false;
        leSigo = false;

        db.collection("friendship")
                .whereEqualTo("followerUid", uidFriend)
                .whereEqualTo("followingUid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   if (task.getResult().isEmpty()) {
                                                       meSigue = false;
                                                       Log.d("control", "no me sigue ", task.getException());
                                                   }else{
                                                       meSigue = true;
                                                       Log.d("control", "si me sigue ", task.getException());
                                                   }
                                                   actualizarBoton();
                                               } else {
                                                   Log.d("control", "Error getting documents: ", task.getException());
                                               }
                                           }
                                       }
                );

        db.collection("friendship")
                .whereEqualTo("followerUid", user.getUid())
                .whereEqualTo("followingUid", uidFriend)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   if (task.getResult() == null || task.getResult().isEmpty()) {
                                                       leSigo = false;
                                                       Log.d("control", "no le sigo ", task.getException());
                                                   }else{
                                                       leSigo = true;
                                                       Log.d("control", "si le sigo ", task.getException());
                                                   }
                                                   actualizarBoton();
                                               } else {
                                                   Log.d("control", "Error getting documents: ", task.getException());
                                               }
                                           }
                                       }
                );



    }

    private void actualizarBoton(){
        Button button = findViewById(R.id.btnFollow);
        if(leSigo){
            button.setBackground(getResources().getDrawable(R.drawable.rectangle_pink));
            button.setTextColor(getResources().getColor(R.color.white));
            if(meSigue){
                button.setText(R.string.following);
            }else{
                button.setText(R.string.followin);
            }
        }
        else {
            button.setBackground(getResources().getDrawable(R.drawable.rectangle_white_pink));
            button.setTextColor(getResources().getColor(R.color.pink2));
            if(meSigue){
                button.setText(R.string.teSigue);
            }else{
                button.setText(R.string.follow);
            }
        }
    }
}
