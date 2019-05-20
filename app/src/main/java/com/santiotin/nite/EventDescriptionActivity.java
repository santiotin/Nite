package com.santiotin.nite;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.santiotin.nite.Models.Event;

import java.util.HashMap;
import java.util.Map;

public class EventDescriptionActivity extends AppCompatActivity {

    private boolean favCollapsed;
    private boolean favPressed;
    private boolean assistPressed;

    private Menu menu;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        event = (Event) getIntent().getSerializableExtra("event");

        favCollapsed = false;
        favPressed = false;
        assistPressed = false;

        iniCollapsingToolbar();
        iniCampos();

        iniFavButtonState();
        iniAssistButtonState();





    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.fav_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;

            case R.id.favItemMenu:
                break;

            default:
                finish();
                break;
        }

        return true;
    }

    public void iniCampos(){

        ImageView imgHeader = findViewById(R.id.imgViewHeader);

        TextView hour = findViewById(R.id.event_hour);
        TextView music = findViewById(R.id.event_music);
        TextView age = findViewById(R.id.event_age);
        TextView dress = findViewById(R.id.event_dress);

        TextView descr = findViewById(R.id.event_descr);
        TextView addr = findViewById(R.id.event_addr);

        TextView numPart = findViewById(R.id.numPart);
        TextView numFri = findViewById(R.id.numFriends);

        RelativeLayout rlPart = findViewById(R.id.rlPart);
        RelativeLayout rlFriends = findViewById(R.id.rlFriends);

        Glide.with(getApplicationContext())
                .load(Uri.parse(event.getUri()))
                .into(imgHeader);

        getSupportActionBar().setTitle(event.getClub() + ": " + event.getName());

        String hora = getResources().getString(R.string.From) + " " + event.getStartHour() + ":00 " + getResources().getString(R.string.to) + " " + event.getEndHour() + ":00";
        hour.setText(hora);

        music.setText(event.getMusic());
        age.setText("+" + event.getAge());
        dress.setText(event.getDress());

        descr.setText(event.getDescription());
        addr.setText(event.getAddress());


        numPart.setText(String.valueOf(event.getNumAssistants()));
        numFri.setText("0");

        rlPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AssistantsActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

    }

    public void iniCollapsingToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        final AppBarLayout apl = findViewById(R.id.app_bar_layout);
        apl.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //Check if the view is collapsed
                if (scrollRange + i == 0) {
                    //collapsed
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, null);
                    upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    favCollapsed = true;
                    if (menu != null){
                        changeFavButtonState();
                    }

                }else{
                    //expanded
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back_white, null);
                    upArrow.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    favCollapsed = false;
                    if (menu != null){
                        changeFavButtonState();
                    }

                }
            }
        });
    }

    public void iniFavButtonState(){
        DocumentReference docRef = db.collection("assistants").document(event.getId() + user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("control", "DocumentSnapshot data: " + document.getData());
                        favPressed = true;
                    } else {
                        Log.d("control", "No such document");
                        favPressed = false;
                    }
                    changeFavButtonState();
                } else {
                    Log.d("control", "get failed with ", task.getException());
                }
            }
        });
    }

    public void changeFavButtonState(){

        MenuItem item = menu.findItem(R.id.favItemMenu);
        if (favPressed){
            if(favCollapsed){
                item.setIcon(R.drawable.ic_fav);
            }else{
                item.setIcon(R.drawable.ic_fav_white);
            }
        }else{
            if(favCollapsed){
                item.setIcon(R.drawable.ic_fav_unpress);
            }else{
                item.setIcon(R.drawable.ic_fav_white_unpress);
            }
        }
    }

    public void iniAssistButtonState(){
        DocumentReference docRef = db.collection("assistants").document(event.getId() + user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("control", "DocumentSnapshot data: " + document.getData());
                        assistPressed = true;
                    } else {
                        Log.d("control", "No such document");
                        assistPressed = false;
                    }
                    changeAssistButtonState();
                } else {
                    Log.d("control", "get failed with ", task.getException());
                }
            }
        });
    }

    public void changeAssistButtonState(){
        Button assistBtn = findViewById(R.id.assistBtn);
        assistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialogAssist();
            }
        });

        if (assistPressed){
            assistBtn.setText(getResources().getString(R.string.confirmado));
            assistBtn.setTextColor(getResources().getColor(R.color.white));
            assistBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_pink));
        }else {
            assistBtn.setText(getResources().getString(R.string.assistir));
            assistBtn.setTextColor(getResources().getColor(R.color.pink2));
            assistBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_white_pink));
        }

    }

    public void showConfirmationDialogAssist(){
        if (assistPressed){

            AlertDialog.Builder builder = new AlertDialog.Builder(EventDescriptionActivity.this)
                    .setTitle("Cancelar Asistencia")
                    .setMessage("¿Quieres cancelar la assistencia a este evento?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            db.collection("assistants").document(event.getId() + user.getUid())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("control", "DocumentSnapshot successfully deleted!");
                                            assistPressed = false;
                                            changeFavButtonState();
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
                    .setIcon(R.drawable.ic_calendar_black);
            AlertDialog alert1 = builder.create();
            alert1.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDescriptionActivity.this)
                    .setTitle("Confirmar assistencia")
                    .setMessage("¿Quieres confirmar la assistencia a este evento?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            Map<String, Object> assist = new HashMap<>();
                            assist.put("eid", event.getId());
                            assist.put("uid", user.getUid());
                            assist.put("eventName", event.getName());
                            assist.put("userName", user.getDisplayName());
                            assist.put("club", event.getClub());

                            db.collection("assistants").document(event.getId() + user.getUid())
                                    .set(assist)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("control", "DocumentSnapshot successfully written!");
                                            assistPressed = true;
                                            changeFavButtonState();
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
                    .setIcon(R.drawable.ic_calendar_black);
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

}

