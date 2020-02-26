package com.example.fakemessenger.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fakemessenger.R;
import com.example.fakemessenger.chat.ChatActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);

        ImageView userImage = view.findViewById(R.id.users_list_item_profile_picture_circle_imageView);
        TextView userName = view.findViewById(R.id.users_list_item_username_textView);
        CardView rootLayout = view.findViewById(R.id.users_list_root_layout);
        return new UserViewHolder(view,userName,userImage,rootLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        final User user = users.get(position);

        Picasso.with(context).load(user.getImageUrl())
                .resize(50, 50)
                .into(holder.userImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) holder.userImageView
                                .getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory
                                .create(context.getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        holder.userImageView.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {
                        //image loading error
                        holder.userImageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
                    }
                });


        holder.userNameTextView.setText(user.getUsername());

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                .putExtra("secondUser",user));
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView userImageView;
        CardView rootLayout;

        UserViewHolder(View itemView, TextView CompanyNameTextView,
                       ImageView userImageView, CardView rootLayout) {
            super(itemView);
            this.userNameTextView = CompanyNameTextView;
            this.userImageView = userImageView;
            this.rootLayout = rootLayout;
        }
    }

}
