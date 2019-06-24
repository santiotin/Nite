package com.santiotin.nite.Holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Adapters.GlideApp;
import com.santiotin.nite.R;

public class NotEventHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView date;
    private ImageView imagePerson;
    private ImageView imageEvent;

    public NotEventHolder(final View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tvNameEventRequest);
        date = itemView.findViewById(R.id.tvDateEventRequest);
        imagePerson =  itemView.findViewById(R.id.cirImgViewNotificationEventLeft);
        imageEvent = itemView.findViewById(R.id.imgViewNotificationEventRight);

    }

    public void setName(String n, String title) {
        String txt = n + " ha confirmado su asistencia a " + title;
        name.setText(txt);
    }

    public void setImagePerson(Context context, String eid){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + eid + ".jpg");
        GlideApp.with(context)
                .load(storageRef)
                .error(R.drawable.logo)
                .into(imagePerson);
    }

    public void setImageEvent(Context context, String eid){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + eid + ".jpg");
        GlideApp.with(context)
                .load(storageRef)
                .error(R.drawable.logo)
                .into(imageEvent);
    }

    public void setDate(int day, int month, int year) {
        String text = day + "/" + month + "/" + year;
        date.setText(text);
    }
}
