package com.example.fakemessenger;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CurrentFirebaseUser {
    public static final FirebaseUser getCurrentUser (){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
