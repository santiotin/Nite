package com.santiotin.nite;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        iniToolbar();
        iniCampos();

        RelativeLayout rlName = findViewById(R.id.rlEditName);
        RelativeLayout rlEmail = findViewById(R.id.rlEditEmail);
        RelativeLayout rlTelef = findViewById(R.id.rlEditTelef);
        RelativeLayout rlPasswd = findViewById(R.id.rlEditPasswd);


    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void iniToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.editProfile));
    }

    public void iniCampos(){

        TextView tvname = findViewById(R.id.editName);
        TextView tvemail = findViewById(R.id.editEmail);
        TextView tvtelef = findViewById(R.id.editTelef);
        ImageView imgView = findViewById(R.id.imgViewEditPhoto);

        String name = user.getDisplayName();
        String email = user.getEmail();
        String telef = user.getPhoneNumber();
        Uri photo = user.getPhotoUrl();

        if(name != null && !name.equals("")) {
            tvname.setText(name);
        }

        if(email != null && !email.equals("")) {
            tvemail.setText(email);
        }

        if (telef != null && !telef.equals("")) {
            tvtelef.setText(telef);
        } else {
            tvtelef.setText(getString(R.string.addtelef));
        }

        if (photo != null && !photo.equals(Uri.EMPTY)){
            //imgView.setImageURI(photo);
        }else{
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.event_sutton));
        }

    }
}
