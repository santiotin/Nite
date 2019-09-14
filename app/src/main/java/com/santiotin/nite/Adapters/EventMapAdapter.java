package com.santiotin.nite.Adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Activities.EventDescriptionActivity;
import com.santiotin.nite.Models.Event;
import com.santiotin.nite.R;

import java.util.List;

public class EventMapAdapter extends PagerAdapter {

    private List<Event> events;
    private LayoutInflater layoutInflater;
    private Context context;

    public EventMapAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.item_event_map, container, false);

        TextView title = view.findViewById(R.id.title);
        TextView numAssists = view.findViewById(R.id.assistants);
        ImageView fondo = view.findViewById(R.id.img_card_back);
        CardView cardView = view.findViewById(R.id.cardView);
        TextView club = view.findViewById(R.id.club);

        title.setText(events.get(position).getName());
        club.setText(events.get(position).getClub());
        numAssists.setText(String.valueOf(events.get(position).getNumAssistants()));

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("eventpics/" + events.get(position).getId() + ".jpg");

        GlideApp.with(context)
                .load(storageRef)
                .into(fondo);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), EventDescriptionActivity.class);
                intent.putExtra("event", events.get(position));
                view.getContext().startActivity(intent);
            }
        });



        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
