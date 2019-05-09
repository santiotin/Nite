package com.santiotin.nite.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.R;

import java.util.List;


public class RVCardListAdp extends RecyclerView.Adapter<RVCardListAdp.ViewHolder> {

    private List<Event> events;
    private int layout;
    private OnItemClickListener itemClickListener;
    private Context c;

    public RVCardListAdp(List<Event> events, int layout, OnItemClickListener listener, Context c) {
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
        TextView comp;
        TextView assists;
        ImageView fondo;
        ImageButton btnFriends;
        CardView cardView;

        ViewHolder(final View itemView) {
            // Recibimos la vista completa. La pasa al constructor padre y enlazamos referencias UI
            // con nuestras propiedades ViewHolder declarados justo arriba
            super(itemView);
            title = itemView.findViewById(R.id.title);
            assists =  itemView.findViewById(R.id.assistants);
            fondo =  itemView.findViewById(R.id.img_card_back);
            btnFriends = itemView.findViewById(R.id.imgBtn_friends);
            cardView = itemView.findViewById(R.id.cardView);

        }

        void bind(final Event e, final OnItemClickListener listener, Context c){
            // Procesamos los datos a rellenar
            title.setText(e.getClub() + ": " + e.getName());
            String ass = String.valueOf(e.getNumAssistants()) + " " + itemView.getContext().getString(R.string.participants);
            assists.setText(ass);
            Glide.with(c)
                    .load(Uri.parse(e.getUri()))
                    .into(fondo);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(e, getAdapterPosition());
                }
            });
            btnFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFriendsClick(e, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Event e, int position);
        void onFriendsClick(Event e, int position);
    }
}
