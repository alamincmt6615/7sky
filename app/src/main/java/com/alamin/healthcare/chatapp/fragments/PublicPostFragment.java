package com.alamin.healthcare.chatapp.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alamin.healthcare.chatapp.R;
import com.alamin.healthcare.chatapp.activities.CommentActivity;
import com.alamin.healthcare.chatapp.activities.ProfileActivity;
import com.alamin.healthcare.chatapp.adapters.RetrievePublicPost;
import com.alamin.healthcare.chatapp.models.Comment;
import com.alamin.healthcare.chatapp.models.UploadPostModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PublicPostFragment extends Fragment {

    private DatabaseReference postRef,databaseReference_user,commentReference,likesRef;
    private RecyclerView rv_public_post;
    private FirebaseAuth firebaseAuth,mAuth;
    private RetrievePublicPost retrievePublicPost;
    private String post_id;
    private String user_uid;
    private List<Comment>commentList = new ArrayList<>();

    private CommentActivity commentActivity;
    private Boolean likeChecker = false;
    private String currentUserID;
    private ArrayList<UploadPostModel> publicpostlist = new ArrayList<>();
    private int CountLike;
    public PublicPostFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_public_post, container, false);
        rv_public_post = view.findViewById(R.id.rv_public_post);
        rv_public_post.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            currentUserID = mAuth.getCurrentUser().getUid();
        }


        postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        databaseReference_user = FirebaseDatabase.getInstance().getReference("User");
        commentReference = FirebaseDatabase.getInstance().getReference("Comments");
        likesRef = FirebaseDatabase.getInstance().getReference("Likes");

        //retrieve data for public post
       // retreivePublicPost();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<UploadPostModel>()
                .setQuery(postRef,UploadPostModel.class)
                .build();

        FirebaseRecyclerAdapter<UploadPostModel,PostViewHolder> adapter = new FirebaseRecyclerAdapter<UploadPostModel, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final PostViewHolder postViewHolder, int i, final UploadPostModel uploadPostModel) {
                final String userIDs = getRef(i).getKey();

                postRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("uid")){

                            String name = dataSnapshot.child("name").getValue().toString();
                            String image = dataSnapshot.child("profile_pic_url").getValue().toString();
                            String date = dataSnapshot.child("post_date").getValue().toString();
                            String time = dataSnapshot.child("post_time").getValue().toString();
                            String text = dataSnapshot.child("post_text").getValue().toString();
                             post_id = dataSnapshot.child("uid").getValue().toString();
                             user_uid = dataSnapshot.child("user_uid").getValue().toString();

                            postViewHolder.tv_uer_name_post_layout.setText(name);
                            postViewHolder.tv_comment_date_post_layout.setText(date+" . ");
                            postViewHolder.tv_comment_time_post_layout.setText(time);
                            postViewHolder.tv_comment_text_post_layout.setText(text);

                            Picasso.with(getActivity()).load(image).placeholder(R.drawable.user).into( postViewHolder.iv_profile_pic_post_layout);

                            postViewHolder.iv_profile_pic_post_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    postRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String selected_user_uid = dataSnapshot.child("user_uid").getValue().toString();
                                            Intent intent = new Intent(getActivity(), ProfileActivity.class);
                                            intent.putExtra("userid",selected_user_uid);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });

                            postViewHolder.iv_comment_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), CommentActivity.class);
                                    intent.putExtra("Post_uid",user_uid);
                                    intent.putExtra("Post_id",userIDs);
                                    startActivity(intent);
                                }
                            });

                        }else {
                            Toast.makeText(getActivity(), "empty", Toast.LENGTH_SHORT).show();
//                            String name = dataSnapshot.child("name").getValue().toString();
//                            postViewHolder.tv_uer_name_post_layout.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                commentReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        commentList.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Comment comment = dataSnapshot1.getValue(Comment.class);
                            if (comment != null){
                                if(post_id.equals(comment.getPost_id())) {
                                    commentList.add(comment);
                                }
                            }

                        }
                        if (commentList.size()>0){
                            int counter = commentList.size();
                            postViewHolder.comment_counter.setText(String.valueOf(counter)+"  Comments");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                postViewHolder.setLikeButtonStatus(userIDs);
                postViewHolder.iv_like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "liked", Toast.LENGTH_SHORT).show();
                        likeChecker = true;
                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (likeChecker.equals(true)){
                                    if(dataSnapshot.child(userIDs).hasChild(currentUserID)){
                                      likesRef.child(userIDs).child(currentUserID).removeValue();
                                      likeChecker = false;
                                    }else {
                                      likesRef.child(userIDs).child(currentUserID).setValue(true);
                                      likeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
                PostViewHolder viewHolder = new PostViewHolder(view);
                return viewHolder;
            }
        };
        rv_public_post.setAdapter(adapter);
        adapter.startListening();



       // retreivePublicPost();

    }

    public static class PostViewHolder extends  RecyclerView.ViewHolder{


        private int CountLike;
        private String CurrentUserID;
        private DatabaseReference likeRef;

        TextView tv_uer_name_post_layout;
        TextView tv_comment_date_post_layout;
        TextView tv_comment_time_post_layout;

        TextView tv_comment_text_post_layout;
        CircularImageView iv_profile_pic_post_layout;
        ImageView iv_like_btn,iv_comment_btn;

        TextView comment_counter;
        TextView displayNumberofLIke;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_uer_name_post_layout = itemView.findViewById(R.id.tv_uer_name_post_layout);
            tv_comment_date_post_layout = itemView.findViewById(R.id.tv_comment_date_post_layout);
            tv_comment_time_post_layout = itemView.findViewById(R.id.tv_comment_time_post_layout);
            tv_comment_text_post_layout = itemView.findViewById(R.id.tv_comment_text_post_layout);

            iv_profile_pic_post_layout = itemView.findViewById(R.id.iv_profile_pic_post_layout);

            iv_like_btn = itemView.findViewById(R.id.iv_like_btn);
            iv_comment_btn = itemView.findViewById(R.id.iv_comment_button);

            comment_counter = itemView.findViewById(R.id.comment_counter);
            displayNumberofLIke = itemView.findViewById(R.id.displayNumberofLIke);

            likeRef = FirebaseDatabase.getInstance().getReference("Likes");
            CurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        public void setLikeButtonStatus(final String post_id){
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_id).hasChild(CurrentUserID)){
                       CountLike = (int) dataSnapshot.child(post_id).getChildrenCount();
                       iv_like_btn.setImageResource(R.drawable.ic_heart_red);
                       displayNumberofLIke.setText(Integer.toString(CountLike)+" likes");
                    }else {
                        CountLike = (int) dataSnapshot.child(post_id).getChildrenCount();
                        iv_like_btn.setImageResource(R.drawable.ic_favorite_border_black);
                        displayNumberofLIke.setText(Integer.toString(CountLike)+" likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }




/*

    private void retreivePublicPost() {
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                publicpostlist.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    UploadPostModel uploadPostModel = dataSnapshot1.getValue(UploadPostModel.class);
                    publicpostlist.add(uploadPostModel);
                }
                if (publicpostlist.size()>0){
                    retrievePublicPost = new RetrievePublicPost(publicpostlist,getActivity());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rv_public_post.setLayoutManager(mLayoutManager);
                    rv_public_post.setItemAnimator(new DefaultItemAnimator());
                    rv_public_post.setAdapter(retrievePublicPost);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
*/
}
