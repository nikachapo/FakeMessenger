package com.example.fakemessenger.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fakemessenger.R;
import com.example.fakemessenger.firebase.CurrentFirebaseUser;
import com.example.fakemessenger.firebase.FirebaseDB;
import com.example.fakemessenger.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context context;
    private ArrayList<Chat> chats;

    ChatAdapter(Context context, ArrayList<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);

        CardView rootLayout = view.findViewById(R.id.chat_item_root_layout);
        CircleImageView chatListImage = view.findViewById(R.id.chat_list_item_profile_picture_circle_imageView);
        TextView userName = view.findViewById(R.id.chat_list_item_username_textView);
        TextView lastMessage = view.findViewById(R.id.chat_list_item_last_message_textView);
        TextView lastTime = view.findViewById(R.id.chat_list_item_last_time_textView);

        return new ChatAdapter.ChatViewHolder(view, rootLayout, chatListImage,
                userName, lastMessage, lastTime);

    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ChatViewHolder holder, int position) {
        final Chat chat = chats.get(position);
        if (chat.getLastMessageTimeInMillis() != 0) {
            if (chat.getUser1Id().equals(CurrentFirebaseUser.getCurrentUser().getUid())) {
                Picasso.with(context).load(chat.getUser2ProfilePictureURL())
                        .into(holder.userImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                //image loaded successfully
                            }

                            @Override
                            public void onError() {
                                //image loading error
                                holder.userImageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
                            }
                        });
                holder.userNameTextView.setText(chat.getUser2Name());
            } else {
                Picasso.with(context).load(chat.getUser1ProfilePictureURL())
                        .into(holder.userImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                //image loaded successfully
                            }

                            @Override
                            public void onError() {
                                //image loading error
                                holder.userImageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
                            }
                        });
                holder.userNameTextView.setText(chat.getUser1Name());
            }

            holder.lastMessageTextView.setText(chat.getLastMessage());

            if (!chat.isLastMessageSeen()) {
                holder.lastMessageTextView
                        .setTextColor(ResourcesCompat
                                .getColor(context.getResources(),
                                        R.color.textColorBlack,
                                        null));
            }
            holder.lastTimeTextView.setText(millisInTime(chat.getLastMessageTimeInMillis()));

            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!chat.isLastMessageSeen()) {
                        setLastMessageSeenValueTrue(chat);
                    }
                    getSecondUser(chat);
                }
            });
        }
    }

    private void setLastMessageSeenValueTrue(Chat chat) {
        FirebaseDB.getDatabaseReference()
                .child(FirebaseDB.USERS_KEY)
                .child(chat.getUser1Id())
                .child(FirebaseDB.CHATS_KEY)
                .child(chat.getUser2Id())
                .child("lastMessageSeen")
                .setValue(true);

        FirebaseDB.getDatabaseReference()
                .child(FirebaseDB.USERS_KEY)
                .child(chat.getUser2Id())
                .child(FirebaseDB.CHATS_KEY)
                .child(chat.getUser1Id())
                .child("lastMessageSeen")
                .setValue(true);
    }

    private void getSecondUser(final Chat chat) {
        FirebaseDB.getDatabaseReference()
                .child(FirebaseDB.USERS_KEY)
                .child(chat.getUser2Id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User secondUser = dataSnapshot.getValue(User.class);
                        //open ChatActivity
                        context.startActivity(new Intent(context, ChatActivity.class)
                                .putExtra("secondUser", secondUser)
                        );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }


    static class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView rootLayout;
        CircleImageView userImageView;
        TextView userNameTextView;
        TextView lastMessageTextView;
        TextView lastTimeTextView;

        private ChatViewHolder(@NonNull View itemView, CardView rootLayout,
                               CircleImageView userImageView,
                               TextView userNameTextView,
                               TextView lastMessageTextView,
                               TextView lastTimeTextView) {
            super(itemView);
            this.rootLayout = rootLayout;
            this.userImageView = userImageView;
            this.userNameTextView = userNameTextView;
            this.lastMessageTextView = lastMessageTextView;
            this.lastTimeTextView = lastTimeTextView;
        }
    }

    public static String millisInTime(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date(millis);
        return formatter.format(date);
    }
}
