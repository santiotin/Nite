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
                snapshot.getLong("listsPrice").intValue(),
                snapshot.getLong("ticketsPrice").intValue(),
                snapshot.getLong("vipsPrice").intValue(),
                snapshot.getDouble("lat"),
                snapshot.getDouble("long"),
                snapshot.getString("city"),
                snapshot.getString("webPay"));
        return e;
    }

}
