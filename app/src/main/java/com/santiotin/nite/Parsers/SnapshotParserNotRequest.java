package com.santiotin.nite.Parsers;

import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiotin.nite.Models.NotRequest;

public class SnapshotParserNotRequest implements SnapshotParser<NotRequest> {

    @NonNull
    @Override
    public NotRequest parseSnapshot(@NonNull DocumentSnapshot snapshot) {
        NotRequest r = new NotRequest(
                snapshot.getId(),
                snapshot.getString("personName"),
                snapshot.getLong("day").intValue(),
                snapshot.getLong("month").intValue(),
                snapshot.getLong("year").intValue()
        );
        return r;
    }
}
