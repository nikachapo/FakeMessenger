package com.example.fakemessenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fakemessenger.firebase.CurrentFirebaseUser;
import com.example.fakemessenger.firebase.FirebaseDB;
import com.example.fakemessenger.user.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getName();

    private static final int MY_REQUEST_CODE = 17;
    private List<AuthUI.IdpConfig> providers;

    private Button retryButton;
    private ImageView loginIcon;
    private ProgressBar loginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        loginIcon = findViewById(R.id.login_icon);
        loginProgress = findViewById(R.id.login_progress);

        retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryButton.setVisibility(View.INVISIBLE);
                loginProgress.setVisibility(View.VISIBLE);
                checkInternetConnection();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkInternetConnection();

    }

    private void checkInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            if (CurrentFirebaseUser.getCurrentUser() == null) {
                Log.d(TAG, "User is null - showing sign in options");
                showSignInOption();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    FirebaseDB.getDatabaseReference()
                            .child(FirebaseDB.USERS_KEY)
                            .child(CurrentFirebaseUser.getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(checkIfUserExistsListener);
                }
            }
        }else{
            loginIcon.setImageResource(R.drawable.no_internet_icon);
            retryButton.setVisibility(View.VISIBLE);
            loginProgress.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (CurrentFirebaseUser.getCurrentUser() != null) {
            Log.d(TAG, "OnStop() - removing checkIfUserExistsListener");
            FirebaseDB.getDatabaseReference()
                    .child(FirebaseDB.USERS_KEY)
                    .child(CurrentFirebaseUser.getCurrentUser().getUid())
                    .removeEventListener(checkIfUserExistsListener);
        }
    }

    private void showSignInOption() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
//                        .setIsSmartLockEnabled(false) //set true to save credentials
                        .setTheme(R.style.AuthUiTheme)
                        .setLogo(R.drawable.message_icon)
                        .build(), MY_REQUEST_CODE
        );
    }


    ValueEventListener checkIfUserExistsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange - check if user exists in DB");
            if (!dataSnapshot.exists()) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            } else {
//                User currentUser = dataSnapshot.getValue(User.class);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.putExtra("currentUser",currentUser);
                startActivity(intent);
                finish();
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_LONG).show();

        }
    };
}