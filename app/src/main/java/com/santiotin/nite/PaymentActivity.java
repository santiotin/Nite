package com.santiotin.nite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.santiotin.nite.Models.Event;

public class PaymentActivity extends AppCompatActivity {

    private Event event;
    private int type;
    private int price;
    private int quanty;
    private int total;

    private TextView tvQuanty;
    private TextView tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        event = (Event) getIntent().getSerializableExtra("event");
        type = (int) getIntent().getIntExtra("type", 0);

        iniToolbar();
        iniCampos();

    }

    public void iniCampos(){
        TextView payTitle = findViewById(R.id.paymentTitle);
        TextView payText = findViewById(R.id.paymentText);

        ImageButton addQuanty = findViewById(R.id.imgBtnAddQuanty);
        ImageButton subQuanty = findViewById(R.id.imgBtnSubQuanty);

        tvQuanty = findViewById(R.id.tvPayQuanty);
        tvPrice = findViewById(R.id.tvPayPrice);

        price = 0;
        total = 0;
        quanty = 0;

        if (type == 0) {
            payTitle.setText(getString(R.string.niteList));
            payText.setText(event.getListsDescr());
            price = event.getListsPrice();
        }
        else if (type == 1) {
            payTitle.setText(getString(R.string.ticket));
            payText.setText(event.getTicketsDescr());
            price = event.getTicketsPrice();
        }
        else if(type == 2) {
            payTitle.setText(getString(R.string.vip));
            payText.setText(event.getVipsDescr());
            price = event.getVipsPrice();
        }

        incrementQuanty();

        addQuanty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuanty();
            }
        });

        subQuanty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementQuanty();
            }
        });
    }

    public  void iniToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.reservationData));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return true;
    }

    private void incrementQuanty() {
        if (quanty < 10) {
            quanty += 1;
            total = quanty * price;
            String aux = "" + quanty;
            tvQuanty.setText(aux);
            if (price == 0) tvPrice.setText(getString(R.string.free));
            else tvPrice.setText(total + getString(R.string.eur));
        }

    }

    private void decrementQuanty() {
        if (quanty > 0) {
            quanty -= 1;
            total = quanty * price;
            String aux = "" + quanty;
            tvQuanty.setText(aux);
            if (price == 0) tvPrice.setText(getString(R.string.free));
            else tvPrice.setText(total + getString(R.string.eur));
        }

    }
}
