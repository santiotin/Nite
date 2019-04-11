package com.santiotin.nite;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.santiotin.nite.Fragments.SignInFragment;
import com.santiotin.nite.Fragments.SignUpFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText signUpName,signUpEmail,signUpPswd;
    private Button registrarse;

    private EditText signInEmail,signInPswd;
    private Button entrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        final CustomViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);
        viewPager.disableScroll(true);
        viewPager.invalidate();

        final TextView tvSignIn = findViewById(R.id.tvSignIn);
        final TextView tvSignUp = findViewById(R.id.tvSignUp);


        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() == 1){
                    tvSignIn.setTextColor(getResources().getColor(R.color.pink));
                    tvSignIn.setTextSize(40);
                    tvSignUp.setTextColor(getResources().getColor(R.color.textcolor));
                    tvSignUp.setTextSize(30);
                    viewPager.setCurrentItem(0);

                    signInEmail = findViewById(R.id.emailEditTextSignIn);
                    signInPswd = findViewById(R.id.passwdEditTextSignIn);
                    entrar = findViewById(R.id.btnSignIn);
                    entrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Entra en el onClick", Toast.LENGTH_LONG).show();
                            userLogin();
                        }
                    });
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() == 0) {
                    tvSignIn.setTextColor(getResources().getColor(R.color.textcolor));
                    tvSignIn.setTextSize(30);
                    tvSignUp.setTextColor(getResources().getColor(R.color.pink));
                    tvSignUp.setTextSize(40);
                    viewPager.setCurrentItem(1);


                    registrarse = findViewById(R.id.btnSignUp);
                    signUpName = findViewById(R.id.nameEditTextSignUp); //Para el registro de email+password esto no srive para nada
                    signUpEmail = findViewById(R.id.emailEditTextSignUp);
                    signUpPswd = findViewById(R.id.passwdEditTextSignUp);

                    registrarse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userRegister();
                        }
                    });

                }
            }
        });

    }

    private void userLogin() {

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
                                finish();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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

    private void userRegister() {
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
            Toast.makeText(getApplicationContext(), "Entra en el tercer if ", Toast.LENGTH_LONG).show();
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
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //mAuth.getCurrentUser().sendEmailVerification();
                                //Toast.makeText(getApplicationContext(), "Hemos enviado un mensaje a tu email. Verifícalo.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    //Error de mismo email
                                    Toast.makeText(getApplicationContext(), "El correo ya existe", Toast.LENGTH_SHORT).show();

                                } else {
                                    //Otro tipo de errores
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }

                        }
                    });

        }
    }

    private void setupViewPager(ViewPager viewPager) {

        PruebasActivity.Adapter adapter = new PruebasActivity.Adapter(getSupportFragmentManager());
        adapter.addFragment(new SignInFragment(), getString(R.string.events));
        adapter.addFragment(new SignUpFragment(), getString(R.string.friends));
        viewPager.setAdapter(adapter);


    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
