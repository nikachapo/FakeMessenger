package com.example.fakemessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    @Override
    protected void onStop() {
        super.onStop();

        AuthUI.getInstance()
                .signOut(getApplicationContext())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext()
                                ,"Registration terminated",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                        finish();
                    }
                });
    }
}
