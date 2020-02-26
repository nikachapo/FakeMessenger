package com.example.fakemessenger.user;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class UsersListFragment extends Fragment {
    private Context mContext;
    private RecyclerView.Adapter usersAdapter;
    private RecyclerView usersRecycler;
    private ProgressBar usersProgressBar;
    private SwipeRefreshLayout usersSwipe;

    private DatabaseReference usersRef = FirebaseDB.getDatabaseReference()
            .child(FirebaseDB.USERS_KEY);
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        usersRecycler = view.findViewById(R.id.fragment_users_recyclerView);

        usersProgressBar = view.findViewById(R.id.fragment_users_progressbar);
        usersSwipe = view.findViewById(R.id.fragment_users_swipe_refresh_layout);
        usersSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecyclerViewViewData();
            }
        });

        initRecyclerView();
        updateRecyclerViewViewData();

        return view;

    }

    private void initRecyclerView() {
        //set RecyclerView quality
        usersRecycler.setHasFixedSize(true);
        usersRecycler.setItemViewCacheSize(20);
        usersRecycler.setDrawingCacheEnabled(true);
        usersRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        usersRecycler.setLayoutManager(layoutManager);

        usersAdapter = new UserAdapter(getContext(), getAllUsers());
    }

    private void updateRecyclerViewViewData() {
        usersAdapter = new UserAdapter(Objects.requireNonNull(mContext), getAllUsers());
    }

    private ArrayList<User> getAllUsers() {
        Log.d("getAllCompanies","getting");
        final ArrayList<User> users = new ArrayList<>();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (users.size() == 0) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);

                        if(user!=null&&
                                !user.getuId().equals(CurrentFirebaseUser.getCurrentUser().getUid()))
                        users.add(user);
                    }

                    usersAdapter = new UserAdapter(Objects.requireNonNull(mContext), users);
                    usersRecycler.setAdapter(usersAdapter);
                    usersAdapter.notifyDataSetChanged();
                    usersProgressBar.setVisibility(View.GONE);

                    if (usersSwipe.isRefreshing()) {
                        usersSwipe.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                usersProgressBar.setVisibility(View.GONE);
            }
        });

        return users;
    }
}
