package com.example.fakemessenger.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fakemessenger.R;
import com.example.fakemessenger.firebase.CurrentFirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Message> messages;

    public interface ItemLongClickListener{
        void onItemLongClickListener(long messageId, int position);
    }


    final private ItemLongClickListener itemLongClickListener;
    public MessageAdapter(Context context, ArrayList<Message> messages,
                          ItemLongClickListener itemLongClickListener) {
        this.context = context;
        this.messages = messages;
        this.itemLongClickListener = itemLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderUid().equals(CurrentFirebaseUser.getCurrentUser().getUid())) {
            return 0;
        } else
            return 1;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item_receiver, parent, false);

            ImageView senderImageView = view.findViewById(R.id.message_item_profile_image);
            TextView messageTextView = view.findViewById(R.id.message_item_message);
            TextView messageTime = view.findViewById(R.id.message_item_time_text_view);
            CardView rootLayout = view.findViewById(R.id.message_item_root_layout);
            return new MessagesViewHolderReceiver(view, messageTextView, senderImageView,
                    messageTime, rootLayout);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item_sender, parent, false);

            ImageView senderImageView = view.findViewById(R.id.message_item_profile_image_sender);
            TextView messageTextView = view.findViewById(R.id.message_item_message_sender);
            TextView messageTime = view.findViewById(R.id.message_item_time_text_view_sender);
            CardView rootLayout = view.findViewById(R.id.message_item_root_layout_sender);
            return new MessagesViewHolderSender(view, messageTextView, senderImageView,
                    messageTime, rootLayout);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder mHolder, int position) {

        if (mHolder.getItemViewType() == 1) {
            final MessagesViewHolderReceiver holder = (MessagesViewHolderReceiver) mHolder;
            final Message message = messages.get(position);

            //checking if the previous message belongs this message sender
            if ((messages.size() > 1 && position >=1 && !messages.get(position - 1).getSenderUid()
                    .equals(messages.get(position).getSenderUid())) || position == 0 ) {

                holder.senderImageView.setVisibility(View.VISIBLE);

                Picasso.with(context).load(message.getSenderProfileURL())
                        .resize(35, 35)
                        .placeholder(R.drawable.ic_loop_black_24dp)
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
            } else {
                holder.senderImageView.setVisibility(View.INVISIBLE);
            }

            holder.messageTextView.setText(message.getText());
            holder.timeTextView.setText(ChatAdapter.millisInTime(message.getTimeInMillis()));

            holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemLongClickListener.onItemLongClickListener(message.getTimeInMillis(),
                            holder.getAdapterPosition());
                    return true;
                }
            });
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.timeTextView.getVisibility() == View.GONE) {
                        holder.timeTextView.setVisibility(View.VISIBLE);
                    } else {
                        holder.timeTextView.setVisibility(View.GONE);
                    }
                }
            });

        } else {
            final MessagesViewHolderSender holder = (MessagesViewHolderSender) mHolder;
            final Message message = messages.get(position);


                //checking if the previous message belongs this message sender
                if ((messages.size() > 1 && position >=1 && !messages.get(position - 1).getSenderUid()
                        .equals(messages.get(position).getSenderUid())) || position==0 ) {

                    holder.senderImageView.setVisibility(View.VISIBLE);

                    Picasso.with(context).load(message.getSenderProfileURL())
                            .resize(35, 35)
                            .placeholder(R.drawable.ic_loop_black_24dp)
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
                } else {

                    holder.senderImageView.setVisibility(View.INVISIBLE);
                }



            holder.messageTextView.setText(message.getText());
            holder.timeTextView.setText(ChatAdapter.millisInTime(message.getTimeInMillis()));

            holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemLongClickListener.onItemLongClickListener(message.getTimeInMillis(),
                            holder.getAdapterPosition());
                    return true;
                }
            });

            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.timeTextView.getVisibility() == View.GONE) {
                        holder.timeTextView.setVisibility(View.VISIBLE);
                    } else {
                        holder.timeTextView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    static class MessagesViewHolderSender extends RecyclerView.ViewHolder {
        ImageView senderImageView;
        TextView messageTextView;
        TextView timeTextView;
        CardView rootLayout;

        MessagesViewHolderSender(View itemView, TextView CompanyNameTextView,
                                 ImageView senderImageView, TextView timeTextView, CardView rootLayout) {
            super(itemView);
            this.messageTextView = CompanyNameTextView;
            this.senderImageView = senderImageView;
            this.timeTextView = timeTextView;
            this.rootLayout = rootLayout;
        }
    }


    static class MessagesViewHolderReceiver extends RecyclerView.ViewHolder {
        ImageView senderImageView;
        TextView messageTextView;
        TextView timeTextView;
        CardView rootLayout;

        MessagesViewHolderReceiver(View itemView, TextView CompanyNameTextView,
                                   ImageView senderImageView, TextView timeTextView, CardView rootLayout) {
            super(itemView);
            this.messageTextView = CompanyNameTextView;
            this.senderImageView = senderImageView;
            this.timeTextView = timeTextView;
            this.rootLayout = rootLayout;
        }
    }


}
