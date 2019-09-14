package com.santiotin.nite.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.R;

import java.util.HashMap;
import java.util.Map;

public class PayListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private boolean exists;

    private Event event;
    private int price;
    private int quanty;
    private int total;

    private TextView tvQuanty;
    private TextView tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_list);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        event = (Event) getIntent().getSerializableExtra("event");

        iniToolbar();
        consultarEstadoJoin();

    }

    public void iniCampos() {
        TextView payTextDescr = findViewById(R.id.payListDescr);
        TextView payTextEvent = findViewById(R.id.payListTextEvent);
        TextView payTextSchedule = findViewById(R.id.payListTextSchedule);
        TextView payTextNote = findViewById(R.id.tvPayListNote);

        ImageButton addQuanty = findViewById(R.id.imgBtnPayListAddQuanty);
        ImageButton subQuanty = findViewById(R.id.imgBtnPayListSubQuanty);

        RelativeLayout rlBtnPay = findViewById(R.id.rlBtnPayList);
        TextView tvBtnPay = findViewById(R.id.tvBtnPayList);

        tvQuanty = findViewById(R.id.tvPayListQuanty);
        tvPrice = findViewById(R.id.tvPayListPrice);


        payTextDescr.setText(event.getListsDescr());
        String titleAndClub = event.getName() + " by " + event.getClub();
        String schedule = event.getDay() + "/" + event.getMonth() + "/" + event.getYear() + " " +
                getString(R.string.from).toLowerCase() + " " + event.getStartHour() + " " + getString(R.string.to).toLowerCase() + " " + event.getEndHour();

        payTextEvent.setText(titleAndClub);
        payTextSchedule.setText(schedule);
        price = event.getListsPrice();

        String aux = "Ya estás apuntado";
        if (exists) {
            tvBtnPay.setText(aux);
            payTextNote.setVisibility(View.VISIBLE);
        }
        else {
            tvBtnPay.setText(getString(R.string.join));
            payTextNote.setVisibility(View.INVISIBLE);
        }

        quanty = 0;
        total = 0;
        incrementQuanty();

        addQuanty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuanty();
            }
        });

        subQuanty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementQuanty();
            }
        });

        rlBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinList();
            }
        });


    }

    public void iniToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.niteList));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void incrementQuanty() {
        if (quanty < 10) {
            quanty += 1;
            total = quanty * price;
            String aux = "" + quanty;
            tvQuanty.setText(aux);
            if (price == 0) tvPrice.setText(getString(R.string.free));
            else tvPrice.setText(total + getString(R.string.eur));
        }

    }

    private void decrementQuanty() {
        if (quanty > 0) {
            quanty -= 1;
            total = quanty * price;
            String aux = "" + quanty;
            tvQuanty.setText(aux);
            if (price == 0) tvPrice.setText(getString(R.string.free));
            else tvPrice.setText(total + getString(R.string.eur));
        }

    }

    private void joinList(){
        final Map<String, Object> join = new HashMap<>();
        join.put("eventList", true);

        if (exists){
            db.collection("users")
                    .document(user.getUid())
                    .collection("assistingEvents")
                    .document(event.getId())
                    .update("eventList", false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("control", "Eliminado de la lista");
                            Toast.makeText(PayListActivity.this, "Has sido eliminado de la lista Nite", Toast.LENGTH_SHORT).show();
                            consultarEstadoJoin();
                        }
                    });
        }else {
            db.collection("users")
                    .document(user.getUid())
                    .collection("assistingEvents")
                    .document(event.getId())
                    .update("eventList", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("control", "Añadido a la lista");
                            Toast.makeText(PayListActivity.this, "Has sido añadido a la lista Nite", Toast.LENGTH_SHORT).show();
                            consultarEstadoJoin();
                        }
                    });
        }

    }

    private void consultarEstadoJoin(){
        db.collection("users")
                .document(user.getUid())
                .collection("assistingEvents")
                .document(event.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot == null){
                                finish();
                            }else {
                                exists = documentSnapshot.getBoolean("eventList");
                                iniCampos();
                            }
                        } else {
                            Toast.makeText(PayListActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}
