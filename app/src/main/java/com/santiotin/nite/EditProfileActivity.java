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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class EditProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101 ;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView imgViewEditPhoto;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();

        iniToolbar();
        iniCampos();

        RelativeLayout rlName = findViewById(R.id.rlEditName);
        RelativeLayout rlEmail = findViewById(R.id.rlEditEmail);
        RelativeLayout rlTelef = findViewById(R.id.rlEditTelef);
        RelativeLayout rlPasswd = findViewById(R.id.rlEditPasswd);

        imgViewEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, CHOOSE_IMAGE);
            }
        });
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
        imgViewEditPhoto = findViewById(R.id.imgViewEditPhoto);

        String name = user.getDisplayName();
        String email = user.getEmail();
        String telef = user.getPhoneNumber();

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

        String photoUri = getIntent().getStringExtra("uri");
        if (photoUri.equals("null")){
            imgViewEditPhoto.setImageResource(R.drawable.logo);
        }else{
            Glide.with(getApplicationContext())
                    .load(Uri.parse(photoUri))
                    .into(imgViewEditPhoto);
        }

    }

    private void iniUserImage(){
        storageRef.child("profilepics/" + user.getUid() + ".jpg").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'profilepics/uid.jpg'
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(imgViewEditPhoto);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // File not found
                        imgViewEditPhoto.setImageResource(R.drawable.logo);
                    }
                });

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
