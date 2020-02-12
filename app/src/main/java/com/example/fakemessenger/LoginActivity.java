package com.example.fakemessenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.fakemessenger.firebase.FirebaseDB;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getName();

    private static final int MY_REQUEST_CODE = 17;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        providers = Collections.singletonList(new AuthUI.IdpConfig.PhoneBuilder().build());


    }

    ValueEventListener checkIfUserExistsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            } else{
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_LONG).show();

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (CurrentFirebaseUser.getCurrentUser() == null) {
            showSignInOption();
        } else {
            FirebaseDB.getDatabaseReference()
                    .child(FirebaseDB.USERS_KEY)
                    .addListenerForSingleValueEvent(checkIfUserExistsListener);


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDB.getDatabaseReference()
                .child(FirebaseDB.USERS_KEY)
                .removeEventListener(checkIfUserExistsListener);
    }

    private void showSignInOption() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.MyTheme)
                        .build(), MY_REQUEST_CODE
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText(this, "" + user.getPhoneNumber(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}