package com.santiotin.nite;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.santiotin.nite.Adapters.RVFriendsSmallAdapter;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.Models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventDescriptionActivity extends AppCompatActivity {

    private Menu menu;
    private boolean collapsed;
    private boolean pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        pressed = false;
        collapsed= false;

        Event e = (Event) getIntent().getSerializableExtra("event");
        ImageView img = findViewById(R.id.imgViewHeader);
        TextView title = findViewById(R.id.title);
        TextView addr = findViewById(R.id.event_addr);
        TextView hour = findViewById(R.id.event_hour);
        TextView numAss = findViewById(R.id.numAss);
        TextView descr = findViewById(R.id.event_descr);
        Button seemore = findViewById(R.id.seemore);
        //TextView price = findViewById(R.id.event_price);

        List<User> users = new ArrayList<>();
        users.add(new User("Amigo1",R.drawable.event_sutton));
        users.add(new User("Amigo2",R.drawable.event_pacha));
        users.add(new User("Amigo3",R.drawable.event_otto));
        users.add(new User("Amigo4",R.drawable.event_bling));
        users.add(new User("Amigo5",R.drawable.event_sutton));
        users.add(new User("Amigo6",R.drawable.event_pacha));
        users.add(new User("Amigo7",R.drawable.event_otto));
        users.add(new User("Amigo8",R.drawable.event_bling));

        RecyclerView mRecyclerView = findViewById(R.id.recyclerViewFriends);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.Adapter mAdapter = new RVFriendsSmallAdapter(users, R.layout.item_friend_small, new RVFriendsSmallAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event e, int position) {

            }
        });

        // Lo usamos en caso de que sepamos que el layout no va a cambiar de tamaño, mejorando la performance
        mRecyclerView.setHasFixedSize(true);
        // Añade un efecto por defecto, si le pasamos null lo desactivamos por completo
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Enlazamos el layout manager y adaptador directamente al recycler view
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        getSupportActionBar().setTitle(e.getClub() + ": " + e.getName());
        img.setImageResource(e.getImage());
        addr.setText(e.getAddress());
        hour.setText(e.getStartHour() + ":00 - "+ e.getEndHour() + ":00");
        numAss.setText(String.valueOf(e.getNumAssistants()));
        descr.setText(e.getDescription());
        seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> users = new ArrayList<>();
                users.add(new User("Amigo1",R.drawable.event_sutton));
                users.add(new User("Amigo2",R.drawable.event_pacha));
                users.add(new User("Amigo3",R.drawable.event_otto));
                users.add(new User("Amigo4",R.drawable.event_bling));
                users.add(new User("Amigo5",R.drawable.event_sutton));
                users.add(new User("Amigo6",R.drawable.event_pacha));
                users.add(new User("Amigo7",R.drawable.event_otto));
                users.add(new User("Amigo8",R.drawable.event_bling));

                Intent intent = new Intent(v.getContext(), AssistantsActivity.class);
                intent.putExtra("Friends", (Serializable)users);
                startActivity(intent);
            }
        });
        //price.setText(String.valueOf(e.getPrice()) + " " + getString(R.string.forticket));

        final AppBarLayout apl = findViewById(R.id.app_bar_layout);
        apl.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //Check if the view is collapsed
                if (scrollRange + i == 0) {
                    //collapsed
                    //apl.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Light);
                    Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, null);
                    upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    collapsed = true;
                    if (menu != null){
                        changeFavButtonState();
                    }

                }else{
                    //expanded
                    // apl.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
                    Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back_white, null);
                    upArrow.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    collapsed = false;
                    if (menu != null){
                        changeFavButtonState();
                    }

                }
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;

            case R.id.favItemMenu:
                showConfirmationDialog();
                break;

            default:
                finish();
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.fav_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    public void changeFavButtonState(){
        MenuItem item = menu.findItem(R.id.favItemMenu);
        if (pressed){
            if(collapsed){
                item.setIcon(R.drawable.ic_fav_black);
            }else{
                item.setIcon(R.drawable.ic_fav);
            }
        }else{
            if(collapsed){
                item.setIcon(R.drawable.ic_fav_unpress_black);
            }else{
                item.setIcon(R.drawable.ic_fav_unpress);
            }
        }
    }

    public void showConfirmationDialog(){
        if (pressed){

            AlertDialog.Builder builder = new AlertDialog.Builder(EventDescriptionActivity.this)
                    .setTitle("Cancelar Asistencia")
                    .setMessage("¿Quieres cancelar la assistencia a este evento?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            pressed = false;
                            changeFavButtonState();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_calendar_black);
            AlertDialog alert1 = builder.create();
            alert1.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDescriptionActivity.this)
                    .setTitle("Confirmar assistencia")
                    .setMessage("¿Quieres confirmar la assistencia a este evento?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            pressed= true;
                            changeFavButtonState();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_calendar_black);
            AlertDialog alert = builder.create();
            alert.show();
        }

    }
}
