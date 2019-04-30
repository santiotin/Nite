package com.santiotin.nite;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiotin.nite.Adapters.RVFriendsSmallAdapter;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDescriptionActivity extends AppCompatActivity {

    private Menu menu;
    private boolean collapsed;
    private boolean pressed;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        collapsed = false;

        event = (Event) getIntent().getSerializableExtra("event");
        ImageView img = findViewById(R.id.imgViewHeader);
        TextView title = findViewById(R.id.title);
        TextView addr = findViewById(R.id.event_addr);
        TextView hour = findViewById(R.id.event_hour);
        TextView numAss = findViewById(R.id.numAss);
        TextView descr = findViewById(R.id.event_descr);
        Button seemore = findViewById(R.id.seemore);

        iniRecyclerViewFriends();
        iniFavButtonState();





        getSupportActionBar().setTitle(event.getClub() + ": " + event.getName());
        img.setImageResource(event.getImage());
        addr.setText(event.getAddress());
        hour.setText(event.getStartHour() + ":00 - "+ event.getEndHour() + ":00");
        numAss.setText(String.valueOf(event.getNumAssistants()));
        descr.setText(event.getDescription());
        seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> users = new ArrayList<>();
                users.add(new User("Amigo1",R.drawable.event_sutton));
                users.add(new User("Amigo2",R.drawable.event_pacha));
                users.add(new User("Amigo3",R.drawable.event_otto));
                users.add(new User("Amigo4",R.drawable.event_bling));
                users.add(new User("Amigo5",R.drawable.event_sutton));
                users.add(new User("Amigo6",R.drawable.event_pacha));
                users.add(new User("Amigo7",R.drawable.event_otto));
                users.add(new User("Amigo8",R.drawable.event_bling));

                Intent intent = new Intent(v.getContext(), AssistantsActivity.class);
                intent.putExtra("Friends", (Serializable)users);
                startActivity(intent);
            }
        });

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
                    //apl.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Light);
                    Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, null);
                    upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    collapsed = true;
                    if (menu != null){
                        changeFavButtonState();
                    }

                }else{
                    //expanded
                    // apl.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
                    Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back_white, null);
                    upArrow.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    collapsed = false;
                    if (menu != null){
                        changeFavButtonState();
                    }

                }
            }
        });



    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;

            case R.id.favItemMenu:
                showConfirmationDialog();
                break;

            default:
                finish();
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.fav_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    public void changeFavButtonState(){
        MenuItem item = menu.findItem(R.id.favItemMenu);
        if (pressed){
            if(collapsed){
                item.setIcon(R.drawable.ic_fav_black);
            }else{
                item.setIcon(R.drawable.ic_fav);
            }
        }else{
            if(collapsed){
                item.setIcon(R.drawable.ic_fav_unpress_black);
            }else{
                item.setIcon(R.drawable.ic_fav_unpress);
            }
        }
    }

    public void showConfirmationDialog(){
        if (pressed){

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
                                            pressed = false;
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
                            assist.put("name", event.getName());
                            assist.put("club", event.getClub());

                            db.collection("assistants").document(event.getId() + user.getUid())
                                    .set(assist)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("control", "DocumentSnapshot successfully written!");
                                            pressed = true;
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

    public void iniRecyclerViewFriends(){
        List<User> users = new ArrayList<>();
        users.add(new User("Amigo1",R.drawable.event_sutton));
        users.add(new User("Amigo2",R.drawable.event_pacha));
        users.add(new User("Amigo3",R.drawable.event_otto));
        users.add(new User("Amigo4",R.drawable.event_bling));
        users.add(new User("Amigo5",R.drawable.event_sutton));
        users.add(new User("Amigo6",R.drawable.event_pacha));
        users.add(new User("Amigo7",R.drawable.event_otto));
        users.add(new User("Amigo8",R.drawable.event_bling));

        RecyclerView mRecyclerView = findViewById(R.id.recyclerViewFriends);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.Adapter mAdapter = new RVFriendsSmallAdapter(users, R.layout.item_friend_small, new RVFriendsSmallAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event e, int position) {

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

    public void iniFavButtonState(){
        DocumentReference docRef = db.collection("assistants").document(event.getId() + user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("control", "DocumentSnapshot data: " + document.getData());
                        pressed = true;
                    } else {
                        Log.d("control", "No such document");
                        pressed = false;
                    }
                    changeFavButtonState();
                } else {
                    Log.d("control", "get failed with ", task.getException());
                }
            }
        });
    }
}
