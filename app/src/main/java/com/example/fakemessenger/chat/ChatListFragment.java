package com.example.fakemessenger.chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.fakemessenger.R;
import com.example.fakemessenger.firebase.CurrentFirebaseUser;
import com.example.fakemessenger.firebase.FirebaseDB;
import com.example.fakemessenger.user.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ChatListFragment extends Fragment {
    private Context mContext;
    private RecyclerView.Adapter chatsAdapter;
    private RecyclerView chatsRecycler;
    private ProgressBar chatsProgressBar;
    private SwipeRefreshLayout chatsSwipe;

    private DatabaseReference chatRef = FirebaseDB.getDatabaseReference()
            .child(FirebaseDB.USERS_KEY)
            .child(CurrentFirebaseUser.getCurrentUser().getUid())
            .child(FirebaseDB.CHATS_KEY);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        chatsRecycler = view.findViewById(R.id.fragment_chat_recyclerView);

        chatsProgressBar = view.findViewById(R.id.fragment_chat_progressbar);
        chatsSwipe = view.findViewById(R.id.fragment_chat_swipe_refresh_layout);
        chatsSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecyclerViewViewData();
            }
        });

        initRecyclerView();

        chatsAdapter = new ChatAdapter(getContext(), getAllChats());

        updateRecyclerViewViewData();


        return view;

    }

    private void initRecyclerView() {
        //set RecyclerView quality
        chatsRecycler.setHasFixedSize(true);
        chatsRecycler.setItemViewCacheSize(20);
        chatsRecycler.setDrawingCacheEnabled(true);
        chatsRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        chatsRecycler.setLayoutManager(layoutManager);
    }

    private void updateRecyclerViewViewData() {
        chatsAdapter = new ChatAdapter(Objects.requireNonNull(mContext), getAllChats());
    }

    private ArrayList<Chat> getAllChats() {
        Log.d("getAllChats", "getting");
        final ArrayList<Chat> chats = new ArrayList<>();
        chatRef.orderByChild("lastMessageTimeInMillis")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chats.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Chat chat = postSnapshot.getValue(Chat.class);
                            chats.add(0, chat);
                        }
                        chatsAdapter = new ChatAdapter(Objects.requireNonNull(mContext), chats);
                        chatsRecycler.setAdapter(chatsAdapter);
                        chatsAdapter.notifyDataSetChanged();
                        chatsProgressBar.setVisibility(View.GONE);

                        if (chatsSwipe.isRefreshing()) {
                            chatsSwipe.setRefreshing(false);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        chatsProgressBar.setVisibility(View.GONE);
                    }
                });

        return chats;
    }
}
