package com.example.fakemessenger.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDB {
    public static final String USERS_KEY = "users";
    public static final DatabaseReference getDatabaseReference(){
        return FirebaseDatabase.getInstance().getReference();
    }

}
