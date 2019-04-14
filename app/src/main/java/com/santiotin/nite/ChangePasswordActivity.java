package com.santiotin.nite;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.santiotin.nite.LoginActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText emailChange;
    FirebaseAuth mAuth;
    Button resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        emailChange = (EditText) findViewById(R.id.editTextEmailPassword);
        mAuth = FirebaseAuth.getInstance();
        resetPassword = (Button) findViewById(R.id.buttonChangePassword);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailChange.getText().toString().trim();

                if(userEmail.isEmpty()){
                    emailChange.setError("Porfavor introduce tu email");
                }
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(ChangePasswordActivity.this, "Email de cambio de contraseña enviado" , Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                            }
                            else{
                                Toast.makeText(ChangePasswordActivity.this, "No ha sido posible enviar el email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}