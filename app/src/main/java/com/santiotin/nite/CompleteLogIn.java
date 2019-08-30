package com.santiotin.nite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CompleteLogIn extends AppCompatActivity {

    private EditText phoneEditText, ageEditText;
    private Button finalizarComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_log_in);

        phoneEditText = findViewById(R.id.editTextPhoneComplete);
        ageEditText = findViewById(R.id.editTextAgeComplete);

        finalizarComplete = findViewById(R.id.buttonFinalizarComplete);

        finalizarComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneEditText.toString().trim().isEmpty()){
                    phoneEditText.setError("Introduce un número válido");
                }
            }
        });
    }
}
