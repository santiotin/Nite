package com.santiotin.nite.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.R;

import java.util.List;

public class RVPersonEventsAdapter extends RecyclerView.Adapter<RVPersonEventsAdapter.ViewHolder> {

    private List<Event> events;
    private int layout;
    private OnItemClickListener itemClickListener;
    private Context c;

    public RVPersonEventsAdapter(List<Event> events, int layout, OnItemClickListener listener, Context c) {
        this.events = events;
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
        holder.bind(events.get(position), itemClickListener, c);
    }

    @Override
    public int getItemCount() {
        // return tamaño de la lista que tenemos
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Elementos UI a rellenar
        TextView title;
        TextView club;
        ImageView fondo;
        RelativeLayout rlEvent;
        RelativeLayout rlTicket;

        ViewHolder(final View itemView) {
            // Recibimos la vista completa. La pasa al constructor padre y enlazamos referencias UI
            // con nuestras propiedades ViewHolder declarados justo arriba
            super(itemView);
            title = itemView.findViewById(R.id.title);
            club = itemView.findViewById(R.id.club);
            fondo =  itemView.findViewById(R.id.img_card_back);
            rlEvent = itemView.findViewById(R.id.rlSeeEvent);

        }

        void bind(final Event e, final OnItemClickListener listener, Context c){
            // Procesamos los datos a rellenar
            title.setText(e.getName());
            club.setText(e.getClub());
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + e.getId() + ".jpg");

            GlideApp.with(c)
                    .load(storageRef)
                    .into(fondo);

            rlEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(e, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Event e, int position);
    }
}
