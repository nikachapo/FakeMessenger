package com.example.fakemessenger.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CurrentFirebaseUser {
    public static FirebaseUser getCurrentUser (){
        return FirebaseAuth.getInstance().getCurrentUser();
    }



}
