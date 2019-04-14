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

public class ChangePasswordActivity extends AppCompatActivity {

    EditText editTextEmail;
    FirebaseAuth mAuth;
    Button btnResetPasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextEmail = (EditText) findViewById(R.id.editTextEmailPassword);
        mAuth = FirebaseAuth.getInstance();
        btnResetPasswd = (Button) findViewById(R.id.buttonChangePassword);

        btnResetPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = editTextEmail.getText().toString().trim();

                if(userEmail.isEmpty()){
                    editTextEmail.setError(getString(R.string.emailRequired));
                }
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(ChangePasswordActivity.this, getString(R.string.emailChangePasswd), Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                            }
                            else{
                                Toast.makeText(ChangePasswordActivity.this, getString(R.string.emailSendImpossible), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}