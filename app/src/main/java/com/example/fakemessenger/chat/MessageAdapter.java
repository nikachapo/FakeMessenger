package com.example.fakemessenger.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fakemessenger.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessagesViewHolder> {

    private Context context;
    private ArrayList<Message> messages;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

        ImageView senderImageView = view.findViewById(R.id.message_item_profile_image);
        TextView messageTextView = view.findViewById(R.id.message_item_message);
        TextView messageTime = view.findViewById(R.id.message_item_time_text_view);
        CardView rootLayout = view.findViewById(R.id.message_item_root_layout);
        return new MessageAdapter.MessagesViewHolder(view, messageTextView, senderImageView,
                messageTime, rootLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.MessagesViewHolder holder, int position) {
        final Message message = messages.get(position);



        Picasso.with(context).load(message.getSenderProfileURL())
                .resize(50, 50)
                .into(holder.senderImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) holder.senderImageView
                                .getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory
                                .create(context.getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        holder.senderImageView.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {
                        //image loading error
                        holder.senderImageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
                    }
                });

        holder.messageTextView.setText(message.getText());
        holder.timeTextView.setText(ChatAdapter.millisInTime(message.getTimeInMillis()));

//        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    static class MessagesViewHolder extends RecyclerView.ViewHolder {
        ImageView senderImageView;
        TextView messageTextView;
        TextView timeTextView;
        CardView rootLayout;

        MessagesViewHolder(View itemView, TextView CompanyNameTextView,
                           ImageView senderImageView, TextView timeTextView, CardView rootLayout) {
            super(itemView);
            this.messageTextView = CompanyNameTextView;
            this.senderImageView = senderImageView;
            this.timeTextView = timeTextView;
            this.rootLayout = rootLayout;
        }
    }
}
