package com.santiotin.nite;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Map;

public class PersonProfileActivity extends AppCompatActivity {

    private String uidFriend;
    private String nameFriend;
    private Uri uriFriend;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private Button followBtn;

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

        followBtn = findViewById(R.id.btnFollow);

        iniToolbar();
        iniCampos();
        consultarRelacion();

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
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

        db.collection("users")
                .document(user.getUid())
                .collection("followers")
                .document(uidFriend)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                meSigue = true;
                                Log.d("control", "DocumentSnapshot data: " + document.getData());
                            } else {
                                meSigue = false;
                                Log.d("control", "No such document");
                            }
                            actualizarBoton();
                        } else {
                            Log.d("control", "get failed with ", task.getException());
                        }
                    }
                });

        db.collection("users")
                .document(user.getUid())
                .collection("following")
                .document(uidFriend)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                leSigo = true;
                                Log.d("control", "DocumentSnapshot data: " + document.getData());
                            } else {
                                leSigo = false;
                                Log.d("control", "No such document");
                            }
                            actualizarBoton();
                        } else {
                            Log.d("control", "get failed with ", task.getException());
                        }
                    }
                });


    }

    private void actualizarBoton(){
        if(leSigo){
            followBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_pink));
            followBtn.setTextColor(getResources().getColor(R.color.white));
            if(meSigue){
                followBtn.setText(R.string.following);
            }else{
                followBtn.setText(R.string.followin);
            }
        }
        else {
            followBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_white_pink));
            followBtn.setTextColor(getResources().getColor(R.color.pink2));
            if(meSigue){
                followBtn.setText(R.string.teSigue);
            }else{
                followBtn.setText(R.string.follow);
            }
        }
    }

    public void showConfirmationDialog(){
        if (leSigo){

            AlertDialog.Builder builder = new AlertDialog.Builder(PersonProfileActivity.this)
                    .setTitle("Dejar de seguir")
                    .setMessage("¿Quieres dejar de seguir a este usuario?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            db.collection("users")
                                    .document(user.getUid())
                                    .collection("following")
                                    .document(uidFriend)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("control", "DocumentSnapshot successfully deleted!");
                                            leSigo = false;
                                            actualizarBoton();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("control", "Error deleting document", e);
                                        }
                                    });


                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_profile);
            AlertDialog alert1 = builder.create();
            alert1.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(PersonProfileActivity.this)
                    .setTitle("Empezar a seguir")
                    .setMessage("¿Quieres empezar a seguir a este usuario?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            Map<String, Object> assist = new HashMap<>();
                            assist.put("followingName", nameFriend);

                            db.collection("users")
                                    .document(user.getUid())
                                    .collection("following")
                                    .document(uidFriend)
                                    .set(assist)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("control", "DocumentSnapshot successfully written!");
                                            leSigo = true;
                                            actualizarBoton();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("control", "Error writing document", e);
                                        }
                                    });


                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_profile);
            AlertDialog alert = builder.create();
            alert.show();
        }

    }
}
