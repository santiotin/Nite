package com.santiotin.nite.Activities;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.R;
import com.subinkrishna.widget.CircularImageView;

import java.util.ArrayList;

public class FinalizarSignUp extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101 ;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private FirebaseUser fbUser;
    private StorageReference storageRef;

    private ImageView circularImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_sign_up);

        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        circularImageView = findViewById(R.id.cirImgViewEditPhoto);
        progressBar = findViewById(R.id.progressBarEditPhoto);
        progressBar.setVisibility(View.VISIBLE);
        iniUserImage();

        RelativeLayout btnSignUp = findViewById(R.id.btnFinalizar);
        final EditText editTextAge = findViewById(R.id.ageEditTextFinalizar);
        final EditText editTextCity = findViewById(R.id.cityEditTextFinalizar);
        final EditText editTextPhone = findViewById(R.id.phoneEditTextFinalizar);


        ImageButton imgBtnEditPhoto = findViewById(R.id.imgBtnEditPhoto);

        imgBtnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, CHOOSE_IMAGE);
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                userRegister(editTextCity, editTextPhone, editTextAge);
            }
        });
    }

    private void userRegister(final EditText cityEditText, final EditText phoneEditText, final EditText ageEditText) {
        final String city = cityEditText.getText().toString().trim();
        final String phone = phoneEditText.getText().toString().trim();
        final String age = ageEditText.getText().toString().trim();

        if (city.isEmpty()) {
            cityEditText.setError("La ciudad es obligatoria");
            progressBar.setVisibility(View.INVISIBLE);
            cityEditText.requestFocus();
        }

        else if (phone.isEmpty()) {
            phoneEditText.setError("El número de teléfono es obligatorio");
            progressBar.setVisibility(View.INVISIBLE);
            phoneEditText.requestFocus();
        }

        else if (phone.length() < 9) {
            phoneEditText.setError("Introduce un número de teléfono válido");
            progressBar.setVisibility(View.INVISIBLE);
            phoneEditText.requestFocus();
        }

        else if (age.isEmpty()) {
            ageEditText.setError("La edad es obligatoria");
            progressBar.setVisibility(View.INVISIBLE);
            ageEditText.requestFocus();

        } else {
            db.collection("users")
                    .document(fbUser.getUid())
                    .update("age", age,
                            "phone", phone,
                            "findPhones", getFindPhones(phone),
                            "city", city)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("control", "DocumentSnapshot: all fields successfully updated!");

                            Intent intent = new Intent(FinalizarSignUp.this, FindFriendsActivity.class);
                            intent.putExtra("control", "0");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("control", "Error updating document fields", e);
                        }
                    });


        }
    }

    public ArrayList<String> getFindPhones(String phone) {

        ArrayList<String> result = new ArrayList<>();
        result.add(phone);
        String aux;
        if (phone.charAt(0) == '+') {

            aux = phone.subSequence(3, phone.length()).toString();
            result.add(aux);
        } else {

            aux = "+34" + phone;
            result.add(aux);
        }

        return result;

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

        Log.d("control" , "llego y no hago na");
        storageRef.child("profilepics/" + fbUser.getUid() + ".jpg").putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Long photoTime = System.currentTimeMillis();
                iniUserImage();
                /*db.collection("users")
                        .document(fbUser.getUid())
                        .update("photoTime", photoTime)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("control", "photoTime updated");
                                iniUserImage();
                            }
                        });*/
            }
        });


    }

    private void iniUserImage(){

        progressBar.setVisibility(View.VISIBLE);
        final Long photoTime = System.currentTimeMillis();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + fbUser.getUid() + ".jpg");
        GlideApp.with(getApplicationContext())
                .load(storageRef)
                .placeholder(progressBar.getIndeterminateDrawable())
                .signature(new ObjectKey(photoTime))
                .error(R.drawable.logo)
                .into(circularImageView);

        progressBar.setVisibility(View.INVISIBLE);

    }


}
