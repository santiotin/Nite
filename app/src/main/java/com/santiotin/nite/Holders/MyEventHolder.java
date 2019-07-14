package com.santiotin.nite.Holders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.R;
import com.subinkrishna.widget.CircularImageView;

public class MyEventHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView club;
    private CircularImageView fondo;
    public RelativeLayout rlSeeEvent;
    public ImageButton imgBtnList;
    public ImageButton imgBtnTicket;
    public Button btnVip;

    public MyEventHolder(final View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        club = itemView.findViewById(R.id.club);
        fondo = itemView.findViewById(R.id.img_card_back);
        rlSeeEvent = itemView.findViewById(R.id.rlSeeEvent);

        imgBtnList = itemView.findViewById(R.id.imgBtnList);
        imgBtnTicket = itemView.findViewById(R.id.imgBtnTicket);
        btnVip = itemView.findViewById(R.id.btnVip);


    }

    public void setTitleAndClub(String txt, String c){
        title.setText(txt);
        club.setText(c);
    }

    public void setFondo(Context context, String eid){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + eid + ".jpg");

        GlideApp.with(context)
                .load(storageRef)
                .into(fondo);
    }

}

