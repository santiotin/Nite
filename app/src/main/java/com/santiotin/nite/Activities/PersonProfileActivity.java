package com.santiotin.nite.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.signature.ObjectKey;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.Holders.HistoryEventHolder;
import com.santiotin.nite.Models.HistoryEvent;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.Parsers.SnapshotParserHistoryEvent;
import com.santiotin.nite.Parsers.SnapshotParserUser;
import com.santiotin.nite.R;
import com.subinkrishna.widget.CircularImageView;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PersonProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter fbAdapter;

    private User mUser;

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

        mUser = (User)getIntent().getSerializableExtra("user");

        followBtn = findViewById(R.id.btnFollow);

        iniToolbar();
        listenUser();
        iniRecyclerView();

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
        TextView tvcity = findViewById(R.id.cityFriend);
        TextView tvage = findViewById(R.id.ageFriend);
        TextView mEvents = findViewById(R.id.numEventsFriend);
        TextView nFollowers = findViewById(R.id.numfollowersFriend);
        TextView nFollowing = findViewById(R.id.numfollowingFriend);
        TextView textEvents = findViewById(R.id.textEventsFriend);
        TextView textFollowers = findViewById(R.id.textFollowersFriend);
        TextView textFollowing = findViewById(R.id.textFollowingFriend);
        CircularImageView image = findViewById(R.id.imgViewCirclePerson);

        RelativeLayout rlEvents = findViewById(R.id.rlPersonEvents);
        RelativeLayout rlFollowers = findViewById(R.id.rlPersonFollowers);
        RelativeLayout rlFollowing = findViewById(R.id.rlPersonFollowing);


        if (mUser != null){
            tvname.setText(mUser.getName());
            tvcity.setText(mUser.getCity());
            tvage.setText(mUser.getAge());
            mEvents.setText(String.valueOf(mUser.getNumEvents()));
            nFollowers.setText(String.valueOf(mUser.getNumFollowers()));
            nFollowing.setText(String.valueOf(mUser.getNumFollowing()));

            if(mUser.getNumEvents() == 1){
                textEvents.setText(getString(R.string.event));
            }else {
                textEvents.setText(getString(R.string.events));
            }

            if (mUser.getNumFollowers() == 1){
                textFollowers.setText(getString(R.string.follower));
            }else {
                textFollowers.setText(getString(R.string.followers));
            }

            if (mUser.getNumFollowing() == 1){
                textFollowing.setText(getString(R.string.following1));
            }else {
                textFollowing.setText(getString(R.string.following));
            }

        }else {
            tvname.setText(user.getDisplayName());
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + mUser.getUid() + ".jpg");
        GlideApp.with(getApplicationContext())
                .load(storageRef)
                .signature(new ObjectKey(mUser.getPhotoTime()))
                .error(R.drawable.logo)
                .into(image);

        rlEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PersonEventsActivity.class);
                intent.putExtra("uidFriend", mUser.getUid());
                startActivity(intent);
            }
        });

        rlFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PersonFollowersActivity.class);
                intent.putExtra("uidFriend", mUser.getUid());
                startActivity(intent);
            }
        });

        rlFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PersonFollowingActivity.class);
                intent.putExtra("uidFriend", mUser.getUid());
                startActivity(intent);
            }
        });




    }

    private void consultarRelacion(){
        meSigue = false;
        leSigo = false;

        db.collection("users")
                .document(user.getUid())
                .collection("followers")
                .document(mUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            meSigue = true;
                            Log.d("control", "DocumentSnapshot data: " + documentSnapshot.getData());
                        }else{
                            meSigue = false;
                            Log.d("control", "No such document");

                        }
                        actualizarBoton();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("control", "Error", e);
                    }
                });

        db.collection("users")
                .document(user.getUid())
                .collection("following")
                .document(mUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            leSigo = true;
                            Log.d("control", "DocumentSnapshot data: " + documentSnapshot.getData());
                        }else{
                            leSigo = false;
                            Log.d("control", "No such document");
                        }
                        actualizarBoton();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("control", "Error", e);
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
                            unfollowFriend();

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
                            followFriend();

                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_profile);
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void unfollowFriend(){
        //borrar los 2 documentos
        db.collection("users")
                .document(user.getUid())
                .collection("following")
                .document(mUser.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("users")
                                .document(mUser.getUid())
                                .collection("followers")
                                .document(user.getUid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("control", "DocumentSnapshot successfully deleted!");
                                        leSigo = false;
                                        actualizarBoton();
                                        transactionDecrementFriend();
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
                        Log.w("control", "Error deleting document", e);
                    }
                });
    }

    public void followFriend(){
        Map<String, Object> following = new HashMap<>();
        following.put("followingName", mUser.getName());

        final Map<String, Object> follower = new HashMap<>();
        follower.put("followerName", user.getDisplayName());

        //crear los 2 documentos
        db.collection("users")
                .document(user.getUid())
                .collection("following")
                .document(mUser.getUid())
                .set(following)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("users")
                                .document(mUser.getUid())
                                .collection("followers")
                                .document(user.getUid())
                                .set(follower)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("control", "DocumentSnapshot successfully written!");
                                        leSigo = true;
                                        actualizarBoton();
                                        transactionIncrementFriend();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("control", "Incoherencia!!", e);
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

        sendRequestNotification();





    }

    private void sendRequestNotification(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Timestamp timestamp = new Timestamp(c.getTimeInMillis());

        final Map<String, Object> notification = new HashMap<>();
        notification.put("personName", user.getDisplayName());
        notification.put("day", day);
        notification.put("month", month+1);
        notification.put("year", year);
        notification.put("time", timestamp);

        db.collection("users")
                .document(mUser.getUid())
                .collection("notFriendRequests")
                .document(user.getUid())
                .set(notification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("control", "Notificacion enviada");
                    }
                });
    }

    private void transactionIncrementFriend(){
        final DocumentReference sfDocRef1 = db.collection("users").document(user.getUid());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef1);
                double newPopulation = snapshot.getLong("numFollowing") + 1;
                transaction.update(sfDocRef1, "numFollowing", newPopulation);

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

        final DocumentReference sfDocRef2 = db.collection("users").document(mUser.getUid());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef2);
                double newPopulation = snapshot.getLong("numFollowers") + 1;
                transaction.update(sfDocRef2, "numFollowers", newPopulation);

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

    private void transactionDecrementFriend(){
        final DocumentReference sfDocRef1 = db.collection("users").document(user.getUid());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef1);
                double newPopulation = snapshot.getLong("numFollowing") - 1;
                transaction.update(sfDocRef1, "numFollowing", newPopulation);

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

        final DocumentReference sfDocRef2 = db.collection("users").document(mUser.getUid());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef2);
                double newPopulation = snapshot.getLong("numFollowers") - 1;
                transaction.update(sfDocRef2, "numFollowers", newPopulation);

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

    private void listenUser() {
        final DocumentReference docRef = db.collection("users").document(mUser.getUid());
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
                    SnapshotParserUser spe = new SnapshotParserUser();
                    mUser = spe.parseSnapshot(snapshot);
                    iniCampos();
                    consultarRelacion();

                } else {
                    Log.d("control", "Current data: null");
                }
            }
        });
    }

    public void iniRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerViewPersonHistoryEvents);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);

        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);

        getMyHistoryEvents();
    }

    public void getMyHistoryEvents(){

        final TextView tvHistoryEvents = findViewById(R.id.tvPersonActivityHistoryEvents);
        final ImageView imgViewNoActivity = findViewById(R.id.imgViewPersonProfileActivity);
        imgViewNoActivity.setVisibility(View.INVISIBLE);

        Log.d("control", mUser.getUid());
        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .document(mUser.getUid())
                .collection("historyEvents")
                .orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<HistoryEvent> options = new FirestoreRecyclerOptions.Builder<HistoryEvent>()
                .setQuery(query, new SnapshotParserHistoryEvent())
                .build();

        fbAdapter = new FirestoreRecyclerAdapter<HistoryEvent, HistoryEventHolder>(options) {
            @Override
            public HistoryEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_history_events, parent, false);

                return new HistoryEventHolder(view);
            }


            @Override
            protected void onBindViewHolder(HistoryEventHolder holder, final int position, final HistoryEvent he) {
                String txt = "Se ha apuntado a: " + he.getEventTitle();
                holder.setName(txt);
                holder.setDate(he.getDay(), he.getMonth(), he.getYear());
                holder.setImage(getApplicationContext(), he.getEventId());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() > 0){
                    imgViewNoActivity.setVisibility(View.INVISIBLE);
                    tvHistoryEvents.setVisibility(View.INVISIBLE);
                    Log.d("control", "notEmpty");
                }else{
                    imgViewNoActivity.setVisibility(View.VISIBLE);
                    tvHistoryEvents.setVisibility(View.VISIBLE);
                    Log.d("control", "isEmpty");
                }
            }
        };



        mRecyclerView.setAdapter(fbAdapter);
        fbAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (fbAdapter != null) fbAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (fbAdapter != null) fbAdapter.stopListening();
    }
}
