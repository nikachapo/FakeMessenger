package com.example.fakemessenger.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseDB {
    public static final String USERS_KEY = "users";
    public static final String CHATS_KEY = "chats";

    public static final DatabaseReference getDatabaseReference(){
        return FirebaseDatabase.getInstance().getReference();
    }

    public static StorageReference getStorageReference(){
        return FirebaseStorage.getInstance().getReference("userPictures");
    }
}
