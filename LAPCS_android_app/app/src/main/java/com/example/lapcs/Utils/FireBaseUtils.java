package com.example.lapcs.Utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseUtils {

    private static DatabaseReference mDatabaseRef;
    private static FirebaseDatabase mDatabase;

    public static DatabaseReference getDatabaseRef() {
        if (mDatabaseRef == null) {
            getDatabase();
            mDatabaseRef = mDatabase.getReference();
            mDatabaseRef.keepSynced(true);
        }
        return mDatabaseRef;
    }

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}