package com.santiotin.nite.Holders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.R;
import com.subinkrishna.widget.CircularImageView;

public class EventSearchHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView date;
    private CircularImageView fondo;
    public RelativeLayout relativeLayout;

    public EventSearchHolder(final View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.titleAndClubEventSearch);
        fondo = itemView.findViewById(R.id.cirImgViewEventSearch);
        date = itemView.findViewById(R.id.dateEventSearch);
        relativeLayout = itemView.findViewById(R.id.rlSeeEventSearch);

    }

    public void setTitle(String tit, String club){
        String txt = club + ": " + tit;
        title.setText(txt);
    }

    public void setDate(int day, int month, int year){
        String txt = day + "/" + month + "/" + year;
        date.setText(txt);
    }

    public void setFondo(Context context, String eid){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + eid + ".jpg");

        GlideApp.with(context)
                .load(storageRef)
                .into(fondo);
    }

}

