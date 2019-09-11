package com.santiotin.nite.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.santiotin.nite.R;

import java.util.ArrayList;

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

        RelativeLayout btnSignUp = this.findViewById(R.id.btnFinalizar);
        final EditText editTextAge = this.findViewById(R.id.ageEditTextFinalizar);
        final EditText editTextCity = this.findViewById(R.id.cityEditTextFinalizar);
        final EditText editTextPhone = this.findViewById(R.id.phoneEditTextFinalizar);
        progressBar = this.findViewById(R.id.progressBarFinalizar);
        progressBar.setVisibility(View.INVISIBLE);

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
                    .document(user.getUid())
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

}
