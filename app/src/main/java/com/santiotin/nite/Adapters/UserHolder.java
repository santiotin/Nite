package com.santiotin.nite.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.R;

public class UserHolder extends RecyclerView.ViewHolder{

    private TextView name;
    private ImageView image;

    public UserHolder(final View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tvname);
        image =  itemView.findViewById(R.id.imgViewFriend);

    }

    public void setName(String txt) {
        name.setText(txt);
    }

    public void setImage(Context context, String eid){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + eid + ".jpg");
        GlideApp.with(context)
                .load(storageRef)
                .error(R.drawable.logo)
                .into(image);
    }
}
