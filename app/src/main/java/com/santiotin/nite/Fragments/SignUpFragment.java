package com.santiotin.nite.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.santiotin.nite.LoginActivity;
import com.santiotin.nite.MainActivity;
import com.santiotin.nite.R;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button btnSignUp = view.findViewById(R.id.btnSignUp);
        final EditText signUpName = view.findViewById(R.id.nameEditTextSignUp);
        final EditText signUpEmail = view.findViewById(R.id.emailEditTextSignUp);
        final EditText signUpPswd = view.findViewById(R.id.passwdEditTextSignUp);
        progressBar = view.findViewById(R.id.progresBarSignUp);
        progressBar.setVisibility(View.INVISIBLE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom(getContext(), getView());
                progressBar.setVisibility(View.VISIBLE);
                userRegister(signUpName, signUpEmail, signUpPswd);
            }
        });

        return view;
    }


    private void userRegister(final EditText signUpName, final EditText signUpEmail, final EditText signUpPswd) {
        final String name = signUpName.getText().toString().trim();
        final String email = signUpEmail.getText().toString().trim();
        String password = signUpPswd.getText().toString().trim();

        if (name.isEmpty()){
            signUpName.setError(getString(R.string.nameRequired));
            progressBar.setVisibility(View.INVISIBLE);
            signUpName.requestFocus();
        }

        if (email.isEmpty()){
            signUpEmail.setError(getString(R.string.emailRequired));
            progressBar.setVisibility(View.INVISIBLE);
            signUpEmail.requestFocus();
            return;
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpEmail.setError(getString(R.string.emailValid));
            progressBar.setVisibility(View.INVISIBLE);
            signUpEmail.requestFocus();
            return;
        }

        else if ( password.isEmpty()){
            signUpPswd.setError(getString(R.string.passwdRequired));
            progressBar.setVisibility(View.INVISIBLE);
            signUpPswd.requestFocus();
            return;
        }

        else if(password.length() <6){
            signUpPswd.setError(getString(R.string.passwdValid));
            progressBar.setVisibility(View.INVISIBLE);
            signUpPswd.requestFocus();
            return;
        }

        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // get firebase authentication user
                                FirebaseUser authUser = mAuth.getCurrentUser();
                                authUser.sendEmailVerification();
                                Toast.makeText(getContext(), getString(R.string.emailVerification), Toast.LENGTH_SHORT).show();

                                //Cambiar nombre al firebase user
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();
                                authUser.updateProfile(profileUpdates);

                                // crear cloud user e insertarlo
                                Map<String, Object> cloudUser = new HashMap<>();
                                cloudUser.put("name", name);
                                cloudUser.put("email",email);
                                cloudUser.put("numEvents", 0);
                                db.collection("users").document(authUser.getUid()).set(cloudUser);

                                // desconexion y login
                                mAuth.signOut();
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    //Error de mismo email
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(), getString(R.string.userExisting), Toast.LENGTH_SHORT).show();

                                } else {
                                    //Otro tipo de errores
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }

                        }
                    });

        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
