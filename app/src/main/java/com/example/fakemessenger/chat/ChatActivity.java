package com.example.fakemessenger.chat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fakemessenger.R;
import com.example.fakemessenger.firebase.CurrentFirebaseUser;
import com.example.fakemessenger.firebase.FirebaseDB;
import com.example.fakemessenger.user.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements MessageAdapter.ItemLongClickListener {

    public static final String TAG = Chat.class.getSimpleName();

    private ArrayList<Message> messages;

    private User secondUser;

    private RecyclerView.Adapter messagesAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private EditText messageEditText;
    private Button sendButton;

    private DatabaseReference currentUserChatReference;
    private DatabaseReference secondUserChatReference;

    private ValueEventListener checkIfChatWithIdExistsForCurrentUsesListen;
    private ValueEventListener checkIfChatWithIdExistsForSecondUserListen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.activity_chat_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);
        CircleImageView secondUserImageView = findViewById(R.id.activity_chat_second_user_image);
        TextView secondUserNameTextView = findViewById(R.id.activity_chat_second_user_name);

        secondUser = (User) getIntent().getSerializableExtra("secondUser");

        Picasso.with(getApplicationContext()).load(secondUser.getImageUrl())
                .placeholder(R.drawable.ic_loop_black_24dp)
                .into(secondUserImageView);
        secondUserNameTextView.setText(secondUser.getUsername());

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    sendButton.setEnabled(true);
                } else
                    sendButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void initRecyclerView() {
        RecyclerView messagesRecycler = findViewById(R.id.activity_chat_recycler);
        //set RecyclerView quality
        messagesRecycler.setHasFixedSize(true);
        messagesRecycler.setItemViewCacheSize(20);
        messagesRecycler.setDrawingCacheEnabled(true);
        messagesRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        messagesRecycler.setLayoutManager(layoutManager);

        messagesAdapter = new MessageAdapter(getApplicationContext(), getAllMessages(), this);
        messagesRecycler.setAdapter(messagesAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUserChatReference = FirebaseDB.getDatabaseReference()
                .child(FirebaseDB.USERS_KEY)
                .child(CurrentFirebaseUser.getCurrentUser().getUid())
                .child(FirebaseDB.CHATS_KEY)
                .child(secondUser.getuId());


        secondUserChatReference = FirebaseDB.getDatabaseReference()
                .child(FirebaseDB.USERS_KEY)
                .child(secondUser.getuId())
                .child(FirebaseDB.CHATS_KEY)
                .child(CurrentFirebaseUser.getCurrentUser().getUid());


        FirebaseDB.getDatabaseReference().child(FirebaseDB.USERS_KEY)
                .child(CurrentFirebaseUser.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(getCurrentUserListener);


        initRecyclerView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentUserChatReference.removeEventListener(checkIfChatWithIdExistsForCurrentUsesListen);
        secondUserChatReference.removeEventListener(checkIfChatWithIdExistsForSecondUserListen);
    }


    private ArrayList<Message> getAllMessages() {
        Log.d("getAllMessages", "getting");
        messages = new ArrayList<>();
        DatabaseReference messagesRef = currentUserChatReference.child("messages");

        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "OnChildAdded");
                messages.add(dataSnapshot.getValue(Message.class));
                layoutManager.scrollToPosition(messages.size() - 1);
                messagesAdapter.notifyItemInserted(messages.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "OnChildChanged");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "OnChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "OnChildMoved");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "OnChildCancelled");

            }
        });


        return messages;
    }


    private void sendMessage(User currentUser) {
//        String messageKey = currentUserChatReference.child("messages").push().getKey();

//        if (messageKey!=null)

        {
            long timeInMillis = System.currentTimeMillis();
            Message message = new Message(messageEditText.getText().toString(),
                    timeInMillis, currentUser.getuId(), currentUser.getImageUrl());

            //add message to current User DB
            currentUserChatReference
                    .child("messages")
                    .child(String.valueOf(timeInMillis))
                    .setValue(message);
            //change last message time for current User
            currentUserChatReference
                    .child("lastMessageTimeInMillis")
                    .setValue(message.getTimeInMillis());
            //change last message for current User
            currentUserChatReference
                    .child("lastMessage")
                    .setValue(message.getText());

            //add message to second User DB
            secondUserChatReference
                    .child("messages")
                    .child(String.valueOf(timeInMillis))
                    .setValue(message);

            //change last message time for second User
            secondUserChatReference
                    .child("lastMessageTimeInMillis")
                    .setValue(message.getTimeInMillis());
            //change last message for second User
            secondUserChatReference
                    .child("lastMessage")
                    .setValue(message.getText());

            //set message seen for second user
            FirebaseDB.getDatabaseReference()
                    .child(FirebaseDB.USERS_KEY)
                    .child(secondUser.getuId())
                    .child(FirebaseDB.CHATS_KEY)
                    .child(CurrentFirebaseUser.getCurrentUser().getUid())
                    .child("lastMessageSeen")
                    .setValue(false);

            messageEditText.getText().clear();

        }


    }

    ValueEventListener getCurrentUserListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            final User currentUser = dataSnapshot.getValue(User.class);

            checkIfChatWithIdExistsForCurrentUsesListen = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        //create new chat for current User
                        Chat chat = new Chat(secondUser.getuId(),
                                currentUser.getuId(), secondUser.getuId(),
                                currentUser.getUsername(), secondUser.getUsername(),
                                currentUser.getImageUrl(), secondUser.getImageUrl());
                        FirebaseDB.getDatabaseReference()
                                .child(FirebaseDB.USERS_KEY)
                                .child(currentUser.getuId())
                                .child(FirebaseDB.CHATS_KEY)
                                .child(secondUser.getuId())
                                .setValue(chat);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            checkIfChatWithIdExistsForSecondUserListen = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        //create new chat for second user
                        Chat chat = new Chat(currentUser.getuId(),
                                secondUser.getuId(), currentUser.getuId(),
                                secondUser.getUsername(), currentUser.getUsername(),
                                secondUser.getImageUrl(), currentUser.getImageUrl());


                        FirebaseDB.getDatabaseReference()
                                .child(FirebaseDB.USERS_KEY)
                                .child(secondUser.getuId())
                                .child(FirebaseDB.CHATS_KEY)
                                .child(currentUser.getuId())
                                .setValue(chat);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };


            currentUserChatReference
                    .addListenerForSingleValueEvent(checkIfChatWithIdExistsForCurrentUsesListen);
            secondUserChatReference
                    .addListenerForSingleValueEvent(checkIfChatWithIdExistsForSecondUserListen);

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(currentUser);
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


    @Override
    public void onItemLongClickListener(final long messageId, final int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
        alertDialog.setTitle("Do you wand yo delete message?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference chatRef = FirebaseDB.getDatabaseReference()
                                .child(FirebaseDB.USERS_KEY)
                                .child(CurrentFirebaseUser.getCurrentUser().getUid())
                                .child(FirebaseDB.CHATS_KEY)
                                .child(secondUser.getuId());

                        chatRef.child("lastMessageTimeInMillis").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (messageId == dataSnapshot.getValue(Long.class)
                                && position - 1 != -1) {
                                    Message previousMessage = messages.get(position - 1);
                                    chatRef.child("lastMessageTimeInMillis")
                                            .setValue(previousMessage.getTimeInMillis());
                                    chatRef.child("lastMessage")
                                            .setValue(previousMessage.getText());
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        chatRef.child(FirebaseDB.MESSAGES_KEY)
                                .child(String.valueOf(messageId))
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                messages.remove(position);
                                messagesAdapter.notifyItemRemoved(position);
                                Toast.makeText(getApplicationContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Can't delete message", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messages.size() == 0) {
            FirebaseDB.getDatabaseReference()
                    .child(FirebaseDB.USERS_KEY)
                    .child(CurrentFirebaseUser.getCurrentUser().getUid())
                    .child(FirebaseDB.CHATS_KEY)
                    .child(secondUser.getuId())
                    .removeValue();
        }
    }
}
