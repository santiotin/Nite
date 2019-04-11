package com.santiotin.nite.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.santiotin.nite.LoginActivity;
import com.santiotin.nite.MainActivity;
import com.santiotin.nite.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();

        Button btnSignUp = view.findViewById(R.id.btnSignUp);
        EditText signUpName = view.findViewById(R.id.nameEditTextSignUp); //Para el registro de email+password esto no srive para nada
        final EditText signUpEmail = view.findViewById(R.id.emailEditTextSignUp);
        final EditText signUpPswd = view.findViewById(R.id.passwdEditTextSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister(signUpEmail, signUpPswd);
            }
        });

        return view;
    }


    private void userRegister(final EditText signUpEmail, final EditText signUpPswd) {
        String email = signUpEmail.getText().toString().trim();
        String password = signUpPswd.getText().toString().trim();

        if (email.isEmpty()){
            signUpEmail.setError("Email is required");
            signUpPswd.requestFocus();
            return;
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpEmail.setError("Please, eneter a valid email");
            signUpEmail.requestFocus();
            return;
        }

        else if ( password.isEmpty()){
            Toast.makeText(getContext(), "Entra en el tercer if ", Toast.LENGTH_LONG).show();
            signUpPswd.setError("Password is required");
            signUpPswd.requestFocus();
            return;
        }

        else if(password.length() <6){
            signUpPswd.setError("Minimum length password is 6");
            signUpPswd.requestFocus();
            return;
        }

        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //mAuth.getCurrentUser().sendEmailVerification();
                                //Toast.makeText(getApplicationContext(), "Hemos enviado un mensaje a tu email. Verifícalo.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    //Error de mismo email
                                    Toast.makeText(getContext(), "El correo ya existe", Toast.LENGTH_SHORT).show();

                                } else {
                                    //Otro tipo de errores
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }

                        }
                    });

        }
    }

}
