package com.alamin.healthcare.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alamin.healthcare.chatapp.R;
import com.alamin.healthcare.chatapp.activities.CommentActivity;
import com.alamin.healthcare.chatapp.models.Like;
import com.alamin.healthcare.chatapp.models.UploadPostModel;
import com.alamin.healthcare.chatapp.utils.PreferenceData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RetrievePublicPost extends RecyclerView.Adapter<RetrievePublicPost.ViewHolder> {

    PreferenceData preferenceData;
    DatabaseReference databaseReference;

    private ArrayList<UploadPostModel> list = new ArrayList<>();
    private UploadPostModel uploadPostModel;
    private Context context;

    public RetrievePublicPost(ArrayList<UploadPostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public RetrievePublicPost.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
        return new RetrievePublicPost.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RetrievePublicPost.ViewHolder holder, final int position) {
        uploadPostModel = list.get(position);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uploadPostModel.getUser_uid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // SignUPModel signUPModel = dataSnapshot.getValue(SignUPModel.class);
                holder.tv_uer_name_post_layout.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                Picasso.with(context).load(String.valueOf(dataSnapshot.child("image").getValue())).placeholder(R.drawable.user).into(holder.iv_profile_pic_post_layout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.tv_comment_date_post_layout.setText(uploadPostModel.getPost_date()+" , ");
        holder.tv_comment_time_post_layout.setText(uploadPostModel.getPost_time());
        holder.tv_comment_text_post_layout.setText(uploadPostModel.getPost_text());

//        holder.iv_like_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                databaseReference = FirebaseDatabase.getInstance().getReference("Likes");
//               // databaseReference.child(list.get(position).getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
//               // holder.iv_like_btn.setImageResource(R.drawable.ic_heart_red);
//                //likeStore(FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),uploadPostModel.getUid());
//            }
//        });




        //todo::just for check

        holder.iv_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("Post_uid",uploadPostModel.getUser_uid());
                intent.putExtra("Post_id",uploadPostModel.getUid());
                context.startActivity(intent);
            }
        });

       // likeCheck(holder.iv_like_btn,uploadPostModel.getUid());
    }

//    private void likeStore( String uid,  String post_id) {
//       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Likes");
//       databaseReference.child(post_id).child(uid);
//        Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
//       /*
//
//       databaseReference.child(post_id).addValueEventListener(new ValueEventListener() {
//           @Override
//           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//               for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
//                   if (dataSnapshot1.getValue().toString().equals(uid)){
//                       Toast.makeText(context, "already liked ", Toast.LENGTH_SHORT).show();
//                   }else {
//
//                   }
//               }
//           }
//
//           @Override
//           public void onCancelled(@NonNull DatabaseError databaseError) {
//
//           }
//       });
//
//        */
//    }

    private void likeCheck(final ImageView imageView, String postUid){
        final List<Like> likeList = new ArrayList<>();
        final String fbUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Likes");
        databaseReference.child(postUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                  Like like = dataSnapshot1.getValue(Like.class);
                  likeList.add(like);
               }
               if (likeList.size()>0){
                   for (Like item : likeList){
                       if (item.equals(fbUid)){
                           imageView.setImageResource(R.drawable.ic_heart_red);
                       }
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_uer_name_post_layout;
        TextView tv_comment_date_post_layout;
        TextView tv_comment_time_post_layout;

        TextView tv_comment_text_post_layout;
        //TextView tv_like_count__post_layout;
        //EditText tv_post_comment_post_layout;
        ImageView iv_profile_pic_post_layout,iv_like_btn,iv_comment_btn;
        //ImageButton btn_like_post_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            preferenceData = new PreferenceData(context);
            tv_uer_name_post_layout = itemView.findViewById(R.id.tv_uer_name_post_layout);
            tv_comment_date_post_layout = itemView.findViewById(R.id.tv_comment_date_post_layout);
            tv_comment_time_post_layout = itemView.findViewById(R.id.tv_comment_time_post_layout);
            tv_comment_text_post_layout = itemView.findViewById(R.id.tv_comment_text_post_layout);
            // tv_like_count__post_layout = itemView.findViewById(R.id.tv_like_count__post_layout);
            // tv_post_comment_post_layout = itemView.findViewById(R.id.tv_post_comment_post_layout);
            iv_profile_pic_post_layout = itemView.findViewById(R.id.iv_profile_pic_post_layout);
            iv_like_btn = itemView.findViewById(R.id.iv_like_btn);
            iv_comment_btn = itemView.findViewById(R.id.iv_comment_button);
            //btn_like_post_layout = itemView.findViewById(R.id.btn_like_post_layout);
        }
    }
}
