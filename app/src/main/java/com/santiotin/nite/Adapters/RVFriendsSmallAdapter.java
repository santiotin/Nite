package com.santiotin.nite.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.santiotin.nite.Models.Event;
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
            name.setText(u.getName());
            if (u.getUri() == null){
                image.setImageResource(u.getImage());
            } else {
                Glide.with(c)
                        .load(u.getUri())
                        .into(image);
            }

        }
    }

    public interface OnItemClickListener{
        void onItemClick(Event e, int position);
    }
}
