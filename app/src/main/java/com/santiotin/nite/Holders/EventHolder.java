package com.santiotin.nite.Holders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.R;

public class EventHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView numAssists;
    private ImageView fondo;
    public ImageButton btnFriends;
    public CardView cardView;

    public EventHolder(final View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        numAssists = itemView.findViewById(R.id.assistants);
        fondo = itemView.findViewById(R.id.img_card_back);
        btnFriends = itemView.findViewById(R.id.imgBtn_friends);
        cardView = itemView.findViewById(R.id.cardView);

    }

    public void setTitle(String txt){
        title.setText(txt);
    }

    public void setNumAssists(int num){
        String n = String.valueOf(num);
        String text;
        if(num == 1){
            text = n + " " + itemView.getContext().getResources().getString(R.string.assistant);
        }else{
            text = n + " " + itemView.getContext().getResources().getString(R.string.assistants);
        }
        numAssists.setText(text);
    }

    public void setFondo(Context context, String eid){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + eid + ".jpg");

        GlideApp.with(context)
                .load(storageRef)
                .into(fondo);
    }

}

