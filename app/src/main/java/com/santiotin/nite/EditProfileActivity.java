package com.santiotin.nite;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santiotin.nite.Fragments.ProfileFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101 ;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView imgViewEditPhoto;
    private Uri uriProfileImage;
    private String profileImageUrl;

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

        imgViewEditPhoto = findViewById(R.id.imgViewEditPhoto);

        if (user.getPhotoUrl() != null) {
            Picasso.with(this)
                    .load(mAuth.getCurrentUser().getPhotoUrl().toString())
                    .into(imgViewEditPhoto);
        }
        else{
            profileImageUrl = "android.resource://"+  getPackageName() + "/" +  R.drawable.logo;
            uriProfileImage = Uri.parse(profileImageUrl);
            imgViewEditPhoto.setImageURI(uriProfileImage);
        }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imgViewEditPhoto.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImageToFirebaseStorage() {

        final StorageReference profileImageReference = FirebaseStorage.getInstance().getReference("profilepics/" + mAuth.getCurrentUser().getUid() + ".jpg");

        if(uriProfileImage != null){

            profileImageReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                    if(profileImageUrl!=null){
                        saveUserInformation();
                    }
                }
            });


        }
    }

    private void saveUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if(profileImageUrl!=null){

            UserProfileChangeRequest profile = new
                    UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Picasso.with(EditProfileActivity.this)
                                        .load(mAuth.getCurrentUser().getPhotoUrl().toString())
                                        .into(imgViewEditPhoto);

                            }
                        }
                    });
        }
    }
}
