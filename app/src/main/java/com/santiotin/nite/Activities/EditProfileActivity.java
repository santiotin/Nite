package com.santiotin.nite.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.R;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;


public class EditProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101 ;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private User mUser;
    private ImageView imgViewEditPhoto;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
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
        RelativeLayout rlPhone = findViewById(R.id.rlEditPhone);
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
                updateAge();
            }
        });

        rlPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhone();
            }
        });

        rlCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCity();
            }
        });
    }

    public void iniCampos(){

        TextView tvname = findViewById(R.id.editName);
        TextView tvemail = findViewById(R.id.editEmail);
        TextView tvage = findViewById(R.id.editAge);
        TextView tvcity = findViewById(R.id.editCity);
        TextView tvPhone = findViewById(R.id.editPhone);

        iniUserImage();

        tvname.setText(mUser.getName());
        tvemail.setText(mUser.getEmail());
        tvage.setText(mUser.getAge());
        tvcity.setText(mUser.getCity());
        tvPhone.setText(mUser.getPhone());

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
                .signature(new ObjectKey(mUser.getPhotoTime()))
                .error(R.drawable.logo)
                .into(imgViewEditPhoto);

    }

    private void updateAge(){


        final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(this)
                .minValue(16)
                .maxValue(100)
                .defaultValue(Integer.valueOf(mUser.getAge()))
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(getResources().getColor(R.color.colorAccent))
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.editAge))
                .setView(numberPicker)
                .setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String age = String.valueOf(numberPicker.getValue());
                        db.collection("users")
                                .document(user.getUid())
                                .update("age", age)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("control", "DocumentSnapshot successfully updated!");
                                        mUser.setAge(age);
                                        iniCampos();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("control", "Error updating document", e);
                                    }
                                });
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();


    }

    private void updateCity(){
        final EditText taskEditText = new EditText(this);
        taskEditText.setSingleLine();
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.fab_margin);
        params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.fab_margin);
        taskEditText.setLayoutParams(params);
        container.addView(taskEditText);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.editCity))
                .setMessage(getString(R.string.editCityMessage))
                .setView(container)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String city = String.valueOf(taskEditText.getText());
                        db.collection("users")
                                .document(user.getUid())
                                .update("city", city)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("control", "DocumentSnapshot successfully updated!");
                                        mUser.setCity(city);
                                        iniCampos();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("control", "Error updating document", e);
                                    }
                                });
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create();
        dialog.show();


    }

    private void updatePhone(){
        final EditText taskEditText = new EditText(this);
        taskEditText.setSingleLine();
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.fab_margin);
        params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.fab_margin);
        taskEditText.setLayoutParams(params);
        container.addView(taskEditText);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.editPhone))
                .setMessage(getString(R.string.editPhoneMessage))
                .setView(container)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String phone = String.valueOf(taskEditText.getText());
                        db.collection("users")
                                .document(user.getUid())
                                .update("phone", phone)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("control", "DocumentSnapshot successfully updated!");
                                        mUser.setPhone(phone);
                                        iniCampos();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("control", "Error updating document", e);
                                    }
                                });

                        db.collection("users").document(user.getUid()).update("findPhones",getFindPhones(phone)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("control", "DocumentSnapshot successfully updated!");
                                iniCampos();
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("control", "Error updating document", e);
                            }
                        });
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create();
        dialog.show();


    }

    public ArrayList<String> getFindPhones (String phone){

        ArrayList<String> result = new ArrayList<>();
        result.add(phone);
        String aux;
        if (phone.charAt(0) == '+'){

            aux = phone.subSequence(3, phone.length()).toString();
            result.add(aux);
        }

        else{

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

        storageRef.child("profilepics/" + user.getUid() + ".jpg").putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Long photoTime = System.currentTimeMillis();
                db.collection("users")
                        .document(mUser.getUid())
                        .update("photoTime", photoTime)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("control", "photoTime updated");
                                mUser.setPhotoTime(photoTime);
                                iniUserImage();
                            }
                        });
            }
        });


    }



}
