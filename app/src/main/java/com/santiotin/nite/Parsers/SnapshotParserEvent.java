package com.santiotin.nite.Parsers;

import android.support.annotation.NonNull;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiotin.nite.Models.Event;

public class SnapshotParserEvent implements SnapshotParser<Event> {

    @NonNull
    @Override
    public Event parseSnapshot(@NonNull DocumentSnapshot snapshot) {
        Event e = new Event(snapshot.getId(),
                snapshot.getString("name"),
                snapshot.getString("club"),
                snapshot.getString("addr"),
                snapshot.getString("descr"),
                snapshot.getLong("day").intValue(),
                snapshot.getLong("month").intValue(),
                snapshot.getLong("year").intValue(),
                snapshot.getString("starthour"),
                snapshot.getString("endhour"),
                snapshot.getLong("numAssists").intValue(),
                snapshot.getString("dress"),
                snapshot.getString("age"),
                snapshot.getString("music"),
                snapshot.getBoolean("listsBool"),
                snapshot.getBoolean("ticketsBool"),
                snapshot.getBoolean("vipsBool"),
                snapshot.getString("listsText"),
                snapshot.getString("ticketsText"),
                snapshot.getString("vipsText"),
                snapshot.getString("listsPrice"),
                snapshot.getString("ticketsPrice"),
                snapshot.getString("vipsPrice"),
                snapshot.getDouble("lat"),
                snapshot.getDouble("long"),
                snapshot.getString("city"));
        return e;
    }

}
