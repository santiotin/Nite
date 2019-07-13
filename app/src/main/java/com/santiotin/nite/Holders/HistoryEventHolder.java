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
import com.subinkrishna.widget.CircularImageView;

public class HistoryEventHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView date;
    private CircularImageView image;

    public HistoryEventHolder(final View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tvNameHistoryEvents);
        date = itemView.findViewById(R.id.tvDateHistoryEvents);
        image =  itemView.findViewById(R.id.cirImgViewHistoryEvents);

    }

    public void setName(String txt) {
        name.setText(txt);
    }

    public void setImage(Context context, String eid){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + eid + ".jpg");
        GlideApp.with(context)
                .load(storageRef)
                .error(R.drawable.logo)
                .into(image);
    }

    public void setDate(int day, int month, int year) {
        String text = day + "/" + month + "/" + year;
        date.setText(text);
    }
}
