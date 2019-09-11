package com.santiotin.nite.Activities;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Parsers.SnapshotParserEvent;
import com.santiotin.nite.R;

import net.glxn.qrgen.android.QRCode;

public class QRCodeTicket extends AppCompatActivity {

    private Event mEvent;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_ticket);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        mEvent = (Event)getIntent().getSerializableExtra("event");

        iniToolbar();
        getEvent();


    }

    public void iniToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ticket));
    }

    public void iniCampos(){

        ImageView imagenCodigo = findViewById(R.id.imgViewQRCode);
        TextView title = findViewById(R.id.titleTicket);
        TextView hour = findViewById(R.id.horarioTicket);
        TextView addr = findViewById(R.id.addrTicket);
        TextView date = findViewById(R.id.dateTicket);
        ImageView fondo = findViewById(R.id.imgViewFondoTicket);

        title.setText(mEvent.getClub() + ":" + mEvent.getName());
        hour.setText(getResources().getString(R.string.from) + " " + mEvent.getStartHour() + ":00 " + getResources().getString(R.string.to).toLowerCase() + " " + mEvent.getEndHour() + ":00");
        addr.setText(mEvent.getAddress());

        String day = String.valueOf(mEvent.getDay());
        String month = String.valueOf(mEvent.getMonth());
        String year = String.valueOf(mEvent.getYear());
        date.setText(day + "/" + month + "/" + year);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + mEvent.getId() + ".jpg");
        GlideApp.with(getApplicationContext())
                .load(storageRef)
                .into(fondo);


        String qr = String.valueOf((mEvent.getId() + user.getUid()));
        Bitmap bitmap = QRCode.from(qr).bitmap();

        imagenCodigo.setImageBitmap(bitmap);
    }

    public void getEvent(){
        db.collection("events")
                .document(mEvent.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            SnapshotParserEvent spe = new SnapshotParserEvent();
                            mEvent = spe.parseSnapshot(documentSnapshot);
                            iniCampos();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getEvent();
                    }
                });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return true;
    }
}
