package com.santiotin.nite.Parsers;

import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiotin.nite.Models.Request;

public class SnapshotParserRequest implements SnapshotParser<Request> {

    @NonNull
    @Override
    public Request parseSnapshot(@NonNull DocumentSnapshot snapshot) {
        Request r = new Request(
                snapshot.getId(),
                snapshot.getString("personId"),
                snapshot.getString("personName"),
                snapshot.getLong("day").intValue(),
                snapshot.getLong("month").intValue(),
                snapshot.getLong("year").intValue()
        );
        return r;
    }
}
