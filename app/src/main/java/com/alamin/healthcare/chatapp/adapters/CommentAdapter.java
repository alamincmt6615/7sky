package com.alamin.healthcare.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.alamin.healthcare.chatapp.R;
import com.alamin.healthcare.chatapp.models.Comment;
import com.alamin.healthcare.chatapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private List<Comment> commentList = new ArrayList<>();
    private Comment comment;
    private Context context;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        comment = commentList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(comment.getCommmentator_uid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.tv_commentator_name.setText(user.getName());
                holder.comment_date_and_time.setText(comment.getCmt_date()+" . "+comment.getCmt_time());
                Picasso.with(context).load(user.getImage()).placeholder(R.drawable.user).into(holder.commentator_picture);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.comment_message.setText(comment.getCmt_msg());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView commentator_picture;
        TextView tv_commentator_name;
        TextView comment_date_and_time;
        TextView comment_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentator_picture = itemView.findViewById(R.id.commentator_picture);
            tv_commentator_name = itemView.findViewById(R.id.tv_commentator_name);
            comment_date_and_time = itemView.findViewById(R.id.comment_date_and_time);
            comment_message = itemView.findViewById(R.id.comment_message);
        }
    }
}
