package com.santiotin.nite.Parsers;

import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiotin.nite.Models.HistoryEvent;

public class SnapshotParserHistoryEvent implements SnapshotParser<HistoryEvent> {

    @NonNull
    @Override
    public HistoryEvent parseSnapshot(@NonNull DocumentSnapshot snapshot) {
        HistoryEvent he = new HistoryEvent(
                snapshot.getId(),
                snapshot.getString("eventTitle"),
                snapshot.getString("eventClub"),
                snapshot.getLong("day").intValue(),
                snapshot.getLong("month").intValue(),
                snapshot.getLong("year").intValue()
        );
        return he;
    }
}
