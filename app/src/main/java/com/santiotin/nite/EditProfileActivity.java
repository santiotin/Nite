package com.santiotin.nite;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.Models.User;


public class EditProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101 ;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private User mUser;
    private ImageView imgViewEditPhoto;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        imgViewEditPhoto = findViewById(R.id.imgViewEditPhoto);

        mUser = (User)getIntent().getSerializableExtra("user");


        iniToolbar();
        iniFields();
        iniCampos();

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

    public void iniFields(){
        RelativeLayout rlName = findViewById(R.id.rlEditName);
        RelativeLayout rlEmail = findViewById(R.id.rlEditEmail);
        RelativeLayout rlPasswd = findViewById(R.id.rlEditPasswd);
        RelativeLayout rlAge = findViewById(R.id.rlEditAge);
        RelativeLayout rlCity = findViewById(R.id.rlEditCity);

        rlName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.noEditField), Toast.LENGTH_SHORT).show();
            }
        });
        rlEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.noEditField), Toast.LENGTH_SHORT).show();
            }
        });
        rlPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.noEditField), Toast.LENGTH_SHORT).show();
            }
        });
        rlAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.noEditField), Toast.LENGTH_SHORT).show();
            }
        });
        rlCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.noEditField), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void iniCampos(){

        TextView tvname = findViewById(R.id.editName);
        TextView tvemail = findViewById(R.id.editEmail);
        TextView tvage = findViewById(R.id.editAge);
        TextView tvcity = findViewById(R.id.editCity);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + user.getUid() + ".jpg");
        GlideApp.with(getApplicationContext())
                .load(storageRef)
                .error(R.drawable.logo)
                .into(imgViewEditPhoto);


        tvname.setText(mUser.getName());
        tvemail.setText(mUser.getEmail());
        tvage.setText(mUser.getAge());
        tvcity.setText(mUser.getCity());

        imgViewEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, CHOOSE_IMAGE);
            }
        });

    }

    private void iniUserImage(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + user.getUid() + ".jpg");
        GlideApp.with(getApplicationContext())
                .load(storageRef)
                .error(R.drawable.logo)
                .into(imgViewEditPhoto);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {

            Uri uriProfileImage = data.getData();
            uploadImageToFirebaseStorage(uriProfileImage);

        }
    }

    private void uploadImageToFirebaseStorage(Uri uriProfileImage) {

        storageRef.child("profilepics/" + user.getUid() + ".jpg").putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                iniUserImage();
            }
        });

    }



}
