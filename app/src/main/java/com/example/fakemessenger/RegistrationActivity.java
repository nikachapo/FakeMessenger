package com.example.fakemessenger;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fakemessenger.firebase.CurrentFirebaseUser;
import com.example.fakemessenger.firebase.FirebaseDB;
import com.example.fakemessenger.user.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private Uri mImageUri;
    private CircleImageView profileImageView;
    private TextInputLayout userNameEditText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = findViewById(R.id.activity_registration_progress_bar);

        profileImageView = findViewById(R.id.activity_registration_profile_image_view);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        userNameEditText = findViewById(R.id.activity_registration_username_edit_text);

        Button nextButton = findViewById(R.id.activity_registration_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }


    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);
        final String username = userNameEditText.getEditText().getText().toString().trim();
        final String uId = CurrentFirebaseUser.getCurrentUser().getUid();


        if (!username.isEmpty() && mImageUri != null) {
            //
            //put mImageUri to Firebase Storage with file extension
            final StorageReference pictureReference = FirebaseDB.getStorageReference()
                    .child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            pictureReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful() && task.getException()!=null) {
                        throw task.getException();
                    }

                    return pictureReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful() && task.getResult() != null) {
                        Uri downloadUri = task.getResult();

                        final User user = new User(uId,
                                username,
                                downloadUri.toString());
                        FirebaseDB.getDatabaseReference()
                                .child(FirebaseDB.USERS_KEY)
                                .child(uId)
                                .setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "user registered",
                                                Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(RegistrationActivity.this,
                                                MainActivity.class));
                                    }
                                });
                    }


                }
            });


        } else {
            Toast.makeText(getApplicationContext(), "choose picture and username", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(profileImageView);

        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
