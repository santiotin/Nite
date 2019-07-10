package com.santiotin.nite;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Parsers.SnapshotParserEvent;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EventDescriptionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private boolean favCollapsed;
    private boolean assistPressed;

    private Menu menu;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Event event;
    private TextView numAssistants;
    private TextView textPart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        event = (Event) getIntent().getSerializableExtra("event");
        Boolean complete = (Boolean) getIntent().getSerializableExtra("notComplete");

        favCollapsed = false;
        assistPressed = false;

        iniCollapsingToolbar();
        if(complete == null) iniCampos();
        listenEvent();

        iniAssistButtonState();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.people_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;

            case R.id.peopleItemMenu:
                Intent intent = new Intent(getApplicationContext(), AssistantsFriendsActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
                break;

            default:
                finish();
                break;
        }

        return true;
    }

    public void iniCollapsingToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(event.getClub() + ": " + event.getName());

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));

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
                    if (menu != null) {
                        changeFavButtonState();
                    }

                } else {
                    //expanded
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back_white, null);
                    upArrow.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    favCollapsed = false;
                    if (menu != null) {
                        changeFavButtonState();
                    }

                }
            }
        });
    }

    public void iniCampos() {

        iniMap();

        ImageView imgHeader = findViewById(R.id.imgViewHeader);

        TextView hour = findViewById(R.id.event_hour);
        TextView music = findViewById(R.id.event_music);
        TextView age = findViewById(R.id.event_age);
        TextView dress = findViewById(R.id.event_dress);

        TextView descr = findViewById(R.id.event_descr);
        TextView addr = findViewById(R.id.event_addr);

        textPart = findViewById(R.id.textPart);
        numAssistants = findViewById(R.id.numPart);

        RelativeLayout rlPart = findViewById(R.id.rlPart);

        LinearLayout lllists = findViewById(R.id.lllists);
        LinearLayout lltickets = findViewById(R.id.lltickets);
        LinearLayout llvips = findViewById(R.id.llvips);

        TextView listsDescr = findViewById(R.id.listDescr);
        TextView ticketsDescr = findViewById(R.id.ticketDescr);
        TextView vipsDescr = findViewById(R.id.vipDescr);

        TextView listsPrice = findViewById(R.id.listPrice);
        TextView ticketsPrice = findViewById(R.id.ticketPrice);
        TextView vipsPrice = findViewById(R.id.vipPrice);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + event.getId() + ".jpg");
        GlideApp.with(getApplicationContext())
                .load(storageRef)
                .into(imgHeader);

        getSupportActionBar().setTitle(event.getClub() + ": " + event.getName());

        String dia = getResources().getString(R.string.day) + " " + event.getDay() + "/" + event.getMonth() + "/" + event.getYear();
        String hora = getResources().getString(R.string.from).toLowerCase() + " " + event.getStartHour() + ":00 " + getResources().getString(R.string.to).toLowerCase() + " " + event.getEndHour() + ":00";
        String total = dia + " " + hora;
        hour.setText(total);

        music.setText(event.getMusic());
        age.setText("+" + event.getAge());
        dress.setText(event.getDress());

        descr.setText(event.getDescription());
        addr.setText(event.getAddress());

        changeAssitants(event.getNumAssistants());

        rlPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AssistantsActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

        if (event.hasLists()) {
            listsDescr.setText(event.getListsDescr());
            listsPrice.setText(event.getListsPrice());
        } else {
            lllists.setVisibility(View.GONE);
        }

        if (event.hasTickets()) {
            ticketsDescr.setText(event.getTicketsDescr());
            ticketsPrice.setText(event.getTicketsPrice());
        } else {
            lltickets.setVisibility(View.GONE);
        }

        if (event.hasVips()) {
            vipsDescr.setText(event.getVipsDescr());
            vipsPrice.setText(event.getVipsPrice());
        } else {
            llvips.setVisibility(View.GONE);
        }

    }

    public void changeFavButtonState() {

        MenuItem item = menu.findItem(R.id.peopleItemMenu);
        if (favCollapsed) {
            item.setIcon(R.drawable.ic_people_black);
        } else {
            item.setIcon(R.drawable.ic_people_white);
        }

    }

    public void iniAssistButtonState() {
        db.collection("users")
                .document(user.getUid())
                .collection("assistingEvents")
                .document(event.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            assistPressed = true;
                            Log.d("control", "DocumentSnapshot data: " + documentSnapshot.getData());

                        } else {
                            assistPressed = false;
                            Log.d("control", "No such document");
                        }
                        changeAssistButtonState();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("control", "get failed with ", e);
                    }
                });
    }

    public void changeAssistButtonState() {
        Button assistBtn = findViewById(R.id.assistBtn);
        assistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialogAssist();
            }
        });

        if (assistPressed) {
            assistBtn.setText(getResources().getString(R.string.confirmed));
            assistBtn.setTextColor(getResources().getColor(R.color.white));
            assistBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_pink));
        } else {
            assistBtn.setText(getResources().getString(R.string.assistir));
            assistBtn.setTextColor(getResources().getColor(R.color.pink2));
            assistBtn.setBackground(getResources().getDrawable(R.drawable.rectangle_white_pink));
        }

    }

    public void showConfirmationDialogAssist() {
        if (assistPressed) {

            AlertDialog.Builder builder = new AlertDialog.Builder(EventDescriptionActivity.this)
                    .setTitle("Cancelar Asistencia")
                    .setMessage("¿Quieres cancelar la assistencia a este evento?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            deleteAssistance();
                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_calendar_black);
            AlertDialog alert1 = builder.create();
            alert1.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDescriptionActivity.this)
                    .setTitle("Confirmar assistencia")
                    .setMessage("¿Quieres confirmar la assistencia a este evento?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with add operation
                            addAssistance();
                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_calendar_black);
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    private void deleteAssistance() {
        db.collection("users").document(user.getUid()).collection("assistingEvents").document(event.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("events").document(event.getId()).collection("assistingUsers").document(user.getUid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("control", "DocumentSnapshot successfully deleted!");
                                        assistPressed = false;
                                        changeAssistButtonState();
                                        transactionDecrementAssitants();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("control", "Incoherencia!!!!", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("control", "Error deleting document", e);
                    }
                });


    }

    private void addAssistance() {
        Map<String, Object> eventAssist = new HashMap<>();
        eventAssist.put("eventName", event.getName());
        eventAssist.put("eventClub", event.getClub());

        final Map<String, Object> userAssist = new HashMap<>();
        userAssist.put("userName", user.getDisplayName());


        db.collection("users")
                .document(user.getUid())
                .collection("assistingEvents")
                .document(event.getId())
                .set(eventAssist)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("events")
                                .document(event.getId())
                                .collection("assistingUsers")
                                .document(user.getUid())
                                .set(userAssist)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("control", "DocumentSnapshot successfully written!");
                                        assistPressed = true;
                                        changeAssistButtonState();
                                        transactionIncrementAssitants();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("control", "Incoherencia!!!!!", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("control", "Error writing document", e);
                    }
                });

        sendHistoryNotification();
        sendEventNotification();

    }

    private void sendEventNotification(){

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Timestamp timestamp = new Timestamp(c.getTimeInMillis());

        final Map<String, Object> notification = new HashMap<>();
        notification.put("eventId", event.getId());
        notification.put("eventTitle", event.getName());
        notification.put("eventClub", event.getClub());
        notification.put("personId", user.getUid());
        notification.put("personName", user.getDisplayName());
        notification.put("day", day);
        notification.put("month", month+1);
        notification.put("year", year);
        notification.put("time", timestamp);


        db.collection("users")
                .document(user.getUid())
                .collection("followers")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            db.collection("users")
                                    .document(documentSnapshot.getId())
                                    .collection("notFriendEvents")
                                    .document(event.getId() + user.getUid())
                                    .set(notification)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("control", "Notificacion de evento enviada");
                                        }
                                    });
                        }
                    }
                });

    }

    private void sendHistoryNotification(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Timestamp timestamp = new Timestamp(c.getTimeInMillis());

        final Map<String, Object> notification = new HashMap<>();
        notification.put("eventTitle", event.getName());
        notification.put("eventClub", event.getClub());
        notification.put("day", day);
        notification.put("month", month+1);
        notification.put("year", year);
        notification.put("time", timestamp);

        db.collection("users")
                .document(user.getUid())
                .collection("historyEvents")
                .document(event.getId())
                .set(notification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("control", "Notificacion enviada al historial");
                    }
                });

    }

    private void transactionIncrementAssitants() {
        final DocumentReference sfDocRef = db.collection("events").document(event.getId());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                double newPopulation = snapshot.getLong("numAssists") + 1;
                transaction.update(sfDocRef, "numAssists", newPopulation);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("control", "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("control", "Transaction failure.", e);
            }
        });

        final DocumentReference sfDocRef2 = db.collection("users").document(user.getUid());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef2);
                double newPopulation = snapshot.getLong("numEvents") + 1;
                transaction.update(sfDocRef2, "numEvents", newPopulation);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("control", "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("control", "Transaction failure.", e);
            }
        });
    }

    private void transactionDecrementAssitants() {
        final DocumentReference sfDocRef = db.collection("events").document(event.getId());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                double newPopulation = snapshot.getLong("numAssists") - 1;
                transaction.update(sfDocRef, "numAssists", newPopulation);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("control", "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("control", "Transaction failure.", e);
            }
        });

        final DocumentReference sfDocRef2 = db.collection("users").document(user.getUid());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef2);
                double newPopulation = snapshot.getLong("numEvents") - 1;
                transaction.update(sfDocRef2, "numEvents", newPopulation);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("control", "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("control", "Transaction failure.", e);
            }
        });
    }

    private void listenEvent() {
        final DocumentReference docRef = db.collection("events").document(event.getId());
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
                    SnapshotParserEvent spe = new SnapshotParserEvent();
                    event = spe.parseSnapshot(snapshot);
                    iniCampos();
                } else {
                    Log.d("control", "Current data: null");
                }
            }
        });
    }

    private  void changeAssitants(int num){
        numAssistants.setText(String.valueOf(num));
        if (num == 1) {
            textPart.setText(getResources().getString(R.string.assistant));
        } else {
            textPart.setText(getResources().getString(R.string.assistants));
        }
    }

    private void iniMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        // Add a marker in Sydney and move the camera
        LatLng eventLoc = new LatLng(event.getLati(), event.getLongi());
        Log.d("control", "lat double " + event.getLati());
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(false);
        mMap.addMarker(new MarkerOptions().position(eventLoc).title(event.getClub()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLoc, 15.0f));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(event.getClub()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

    }
}

