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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.santiotin.nite.MainActivity;
import com.santiotin.nite.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private FirebaseAuth mAuth;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mAuth = FirebaseAuth.getInstance();

        final EditText signInEmail = view.findViewById(R.id.emailEditTextSignIn);
        final EditText signInPswd = view.findViewById(R.id.passwdEditTextSignIn);
        Button btnSignIn = view.findViewById(R.id.btnSignIn);

        Button btnSignInGoogle = view.findViewById(R.id.btnSignInGoogle);
        TextView tvForgotPasswd = view.findViewById(R.id.tvForgotPasswd);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin(signInEmail, signInPswd);
            }
        });


        return view;
    }

    private void userLogin(final EditText signInEmail, final EditText signInPswd) {

        String email = signInEmail.getText().toString().trim();
        String password = signInPswd.getText().toString().trim();

        if (email.isEmpty()){

            signInEmail.setError("Se requiere mail");
            signInEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            signInEmail.setError("Entra un correo válido");
            signInEmail.requestFocus();
            return;
        }

        if ( password.isEmpty()){

            signInPswd.setError("Se requiere contraseña");
            signInPswd.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //if(mAuth.getCurrentUser().isEmailVerified()) {
                            getActivity().finish();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //}
                            //else{

                            //  Toast.makeText(getApplicationContext(),  "Has de verificar tu mail",Toast.LENGTH_SHORT).show();
                            //}

                        }
                        else{

                            signInPswd.setError("Email o contraseña incorrectas");
                            signInPswd.requestFocus();
                        }
                    }
                });
    }

}
