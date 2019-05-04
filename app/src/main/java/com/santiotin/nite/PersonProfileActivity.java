package com.santiotin.nite;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private String uid;
    private String name;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        uid = getIntent().getStringExtra("uid");
        name = getIntent().getStringExtra("name");
        uri = Uri.parse(getIntent().getStringExtra("uri"));



        iniToolbar();
        iniCampos();
    }

    public void iniToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return true;
    }

    public void iniCampos(){

        TextView tvname = findViewById(R.id.personName);
        CircularImageView image = findViewById(R.id.imgViewCirclePerson);
        tvname.setText(name);

        if (String.valueOf(uri).equals("null")){
            image.setImageResource(R.drawable.logo);
            image.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        }
        else{
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(image);
        }



    }
}
