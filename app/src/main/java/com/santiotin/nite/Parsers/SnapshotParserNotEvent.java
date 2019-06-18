package com.santiotin.nite.Parsers;

import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiotin.nite.Models.NotEvent;

public class SnapshotParserNotEvent implements SnapshotParser<NotEvent> {

    @NonNull
    @Override
    public NotEvent parseSnapshot(@NonNull DocumentSnapshot snapshot) {
        NotEvent ne = new NotEvent(
                snapshot.getString("personId"),
                snapshot.getString("personName"),
                snapshot.getLong("day").intValue(),
                snapshot.getLong("month").intValue(),
                snapshot.getLong("year").intValue(),
                snapshot.getString("eventId"),
                snapshot.getString("eventTitle"),
                snapshot.getString("eventClub")
        );
        return ne;
    }
}