package com.santiotin.nite.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
        final EditText signUpName = view.findViewById(R.id.nameEditTextSignUp);
        final EditText signUpEmail = view.findViewById(R.id.emailEditTextSignUp);
        final EditText signUpPswd = view.findViewById(R.id.passwdEditTextSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom(getContext(), getView());
                userRegister(signUpName, signUpEmail, signUpPswd);
            }
        });

        return view;
    }


    private void userRegister(final EditText signUpName, final EditText signUpEmail, final EditText signUpPswd) {
        final String name = signUpName.getText().toString().trim();
        String email = signUpEmail.getText().toString().trim();
        String password = signUpPswd.getText().toString().trim();

        if (name.isEmpty()){
            signUpName.setError(getString(R.string.nameRequired));
            signUpName.requestFocus();
        }

        if (email.isEmpty()){
            signUpEmail.setError(getString(R.string.emailRequired));
            signUpEmail.requestFocus();
            return;
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpEmail.setError(getString(R.string.emailValid));
            signUpEmail.requestFocus();
            return;
        }

        else if ( password.isEmpty()){
            signUpPswd.setError(getString(R.string.passwdRequired));
            signUpPswd.requestFocus();
            return;
        }

        else if(password.length() <6){
            signUpPswd.setError(getString(R.string.passwdValid));
            signUpPswd.requestFocus();
            return;
        }

        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification();
                                Toast.makeText(getContext(), getString(R.string.emailVerification), Toast.LENGTH_SHORT).show();

                                //Cambiar nombre
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();

                                user.updateProfile(profileUpdates);
                                mAuth.signOut();

                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    //Error de mismo email
                                    Toast.makeText(getContext(), getString(R.string.emailExisting), Toast.LENGTH_SHORT).show();

                                } else {
                                    //Otro tipo de errores
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
