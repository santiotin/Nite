package com.santiotin.nite.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.R;

import java.util.List;


public class RVFriendsSmallAdapter extends RecyclerView.Adapter<RVFriendsSmallAdapter.ViewHolder> {

    private List<User> users;
    private int layout;
    private OnItemClickListener itemClickListener;
    private Context c;

    public RVFriendsSmallAdapter(List<User> users, int layout, OnItemClickListener listener, Context c) {
        this.users = users;
        this.layout = layout;
        this.itemClickListener = listener;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(users.get(position), itemClickListener, c);
    }

    @Override
    public int getItemCount() {
        // return tamaño de la lista que tenemos
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Elementos UI a rellenar
        TextView name;
        ImageView image;

        ViewHolder(final View itemView) {
            // Recibimos la vista completa. La pasa al constructor padre y enlazamos referencias UI
            // con nuestras propiedades ViewHolder declarados justo arriba
            super(itemView);
            name = itemView.findViewById(R.id.tvname);
            image =  itemView.findViewById(R.id.imgViewFriend);

        }

        void bind(final User u, final OnItemClickListener listener, Context c){
            // Procesamos los datos a rellenar
            Long photoTime = System.currentTimeMillis() / (1000*60);
            name.setText(u.getName());
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + u.getUid() + ".jpg");
            GlideApp.with(c)
                    .load(storageRef)
                    .signature(new ObjectKey(photoTime))
                    .error(R.drawable.logo)
                    .into(image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(u, getAdapterPosition());
                }
            });

        }
    }

    public interface OnItemClickListener{
        void onItemClick(User u, int position);
    }
}
