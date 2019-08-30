package com.santiotin.nite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FinalizarSignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_sign_up);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Button btnSignUp = this.findViewById(R.id.btnFinalizar);
        final EditText age = this.findViewById(R.id.ageEditTextFinalizar);
        final EditText city = this.findViewById(R.id.cityEditTextFinalizar);
        final EditText phone = this.findViewById(R.id.phoneEditTextFinalizar);
        progressBar = this.findViewById(R.id.progressBarFinalizar);
        progressBar.setVisibility(View.INVISIBLE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                userRegister(city, phone, age);
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

        if (phone.isEmpty()) {
            phoneEditText.setError("El número de teléfono es obligatorio");
            progressBar.setVisibility(View.INVISIBLE);
            phoneEditText.requestFocus();
            return;
        }

        else if(phone.length() < 9){

            phoneEditText.setError("Introduce un número de teléfono válido");
            progressBar.setVisibility(View.INVISIBLE);
            phoneEditText.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            ageEditText.setError("La edad es obligatoria");
            progressBar.setVisibility(View.INVISIBLE);
            ageEditText.requestFocus();
            return;
        }

        else{

            db.collection("users")
                    .document(user.getUid())
                    .update("age", age)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("control", "DocumentSnapshot:age successfully updated!");
                            db.collection("users")
                                    .document(user.getUid())
                                    .update("phone", phone)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("control", "DocumentSnapshot:phone successfully updated!");
                                            db.collection("users")
                                                    .document(user.getUid())
                                                    .update("city", city)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("control", "DocumentSnapshot:city  successfully updated!");
                                                            Intent intent = new Intent(FinalizarSignUp.this, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("control", "Error updating document:city", e);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("control", "Error updating document:phone", e);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("control", "Error updating document:age", e);
                        }
                    });


        }
    }

}
