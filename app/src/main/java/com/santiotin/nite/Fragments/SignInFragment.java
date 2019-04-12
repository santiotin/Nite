package com.santiotin.nite.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.santiotin.nite.MainActivity;
import com.santiotin.nite.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    private ProgressBar progressBar;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        final EditText signInEmail = view.findViewById(R.id.emailEditTextSignIn);
        final EditText signInPswd = view.findViewById(R.id.passwdEditTextSignIn);
        Button btnSignIn = view.findViewById(R.id.btnSignIn);
        Button btnSignInGoogle = view.findViewById(R.id.btnSignInGoogle);
        TextView tvForgotPasswd = view.findViewById(R.id.tvForgotPasswd);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin(signInEmail, signInPswd);
            }
        });
        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLoginGoogle();
            }
        });


        return view;
    }

    private void userLogin(final EditText signInEmail, final EditText signInPswd) {

        progressBar.setVisibility(View.VISIBLE);

        String email = signInEmail.getText().toString().trim();
        String password = signInPswd.getText().toString().trim();

        if (email.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
            signInEmail.setError("Se requiere mail");
            signInEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            progressBar.setVisibility(View.INVISIBLE);
            signInEmail.setError("Entra un correo válido");
            signInEmail.requestFocus();
            return;
        }

        if ( password.isEmpty()){
            progressBar.setVisibility(View.INVISIBLE);
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
                            progressBar.setVisibility(View.INVISIBLE);
                            signInPswd.setError("Email o contraseña incorrectas");
                            signInPswd.requestFocus();
                        }
                    }
                });
    }

    private void userLoginGoogle() {
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null){
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuthWithGoogle(account);
                }

            } catch (ApiException e) {
                Toast.makeText(getContext(), String.valueOf(e.getStatusCode()), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            getActivity().finish();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "SignIn Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
