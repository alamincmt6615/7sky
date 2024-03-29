package com.alamin.healthcare.chatapp.activities;

import android.content.Intent;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alamin.healthcare.chatapp.R;
import com.alamin.healthcare.chatapp.holders.UserHolder;
import com.alamin.healthcare.chatapp.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class UsersActivity extends AppCompatActivity
{
    private final String TAG = "CA/UsersActivity";

    private FirebaseRecyclerAdapter adapter;
    Toolbar toolbar_users_list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        toolbar_users_list = findViewById(R.id.toolbar_users_list);

        toolbar_users_list.setTitle("All Users List");
        setSupportActionBar(toolbar_users_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // RecyclerView related

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.users_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing Users database

        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDatabase.keepSynced(true); // For offline use

        // Initializing adapter

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(usersDatabase.orderByChild("name"), User.class).build();

        adapter = new FirebaseRecyclerAdapter<User, UserHolder>(options)
        {
            @Override
            protected void onBindViewHolder(final UserHolder holder, int position, User model)
            {
                final String userid = getRef(position).getKey();

                holder.setHolder(userid);
                holder.getView().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent userProfileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        userProfileIntent.putExtra("userid", userid);
                        startActivity(userProfileIntent);
                    }
                });
            }

            @Override
            public UserHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);

                return new UserHolder(UsersActivity.this, view, getApplicationContext());
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        adapter.startListening();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        adapter.stopListening();
    }

    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
