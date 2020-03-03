package com.alamin.healthcare.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alamin.healthcare.chatapp.R;
import com.alamin.healthcare.chatapp.activities.ProfileActivity;
import com.alamin.healthcare.chatapp.activities.UsersActivity;
import com.alamin.healthcare.chatapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.ViewHolder> implements Filterable {

    List<User> userList = new ArrayList<>();
    List<User> userListfilter = new ArrayList<>();
    Context context;
    User user;
    public FindFriendAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.userListfilter = userList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        user = userListfilter.get(position);
        Picasso.with(context).load(user.getImage()).placeholder(R.drawable.user).into(holder.user_image);
        holder.user_name.setText(user.getName());
        holder.user_status.setText(user.getStatus());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent userProfileIntent = new Intent(context, ProfileActivity.class);
                userProfileIntent.putExtra("userid","abc" );
                context.startActivity(userProfileIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userListfilter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView user_image;
        ImageView user_online;
        TextView user_name;
        TextView user_status;
        TextView user_timestamp;
        RelativeLayout relativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.user_image);
            user_online = itemView.findViewById(R.id.user_online);
            user_name = itemView.findViewById(R.id.user_name);
            user_status = itemView.findViewById(R.id.user_status);
            user_timestamp = itemView.findViewById(R.id.user_timestamp);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
    @Override
    public Filter getFilter() {
        return FindFrndListFilter;
    }
    private Filter FindFrndListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint == null || constraint.length() == 0){
                Log.d("QueryString", constraint + " If true Character Data");
                userListfilter = userList;
            }else {
                Log.d("QueryString", constraint + " If false Character Data");
                List<User> filteredList = new ArrayList<>();
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User user1 : userList){
                    if (user1.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(user1);
                    }
                }
                userListfilter = filteredList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = userListfilter;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userListfilter = (List<User>) results.values;
            notifyDataSetChanged();
        }
    };

}
