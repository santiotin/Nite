package com.santiotin.nite.Parsers;

import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiotin.nite.Models.User;

public class SnapshotParserUser implements SnapshotParser<User> {

    @NonNull
    @Override
    public User parseSnapshot(@NonNull DocumentSnapshot snapshot) {
        User u = new User(
                snapshot.getId(),
                snapshot.getString("name"),
                snapshot.getString("age"),
                snapshot.getString("city"),
                snapshot.getString("email"),
                snapshot.getLong("numEvents"),
                snapshot.getLong("numFollowers"),
                snapshot.getLong("numFollowing"),
                snapshot.getLong("photoTime")
        );
        return u;
    }
}