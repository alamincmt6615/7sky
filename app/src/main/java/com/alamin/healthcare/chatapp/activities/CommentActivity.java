package com.alamin.healthcare.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alamin.healthcare.chatapp.R;
import com.alamin.healthcare.chatapp.adapters.CommentAdapter;
import com.alamin.healthcare.chatapp.models.Comment;
import com.alamin.healthcare.chatapp.models.UploadPostModel;
import com.alamin.healthcare.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CommentActivity extends AppCompatActivity{
    private Toolbar toolbar_comment_section;
    private CircularImageView iv_profile_pic_comment_layout,post_commentator_picture;
    private TextView tv_post_text_comment_layout,tv_post_date_comment_layout,tv_post_time_comment_layout,tv_uer_name_comment_layout;
    private EditText comment_message;
    private Button comment_send;
    private RecyclerView rv_comment_list;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String uid,post_uid,post_id;
    private List<Comment> commentList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        toolbar_comment_section = findViewById(R.id.toolbar_comment_section);
        setSupportActionBar(toolbar_comment_section);
        toolbar_comment_section.setTitle("Comment Section");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();

        initView();
        post_id = getIntent().getStringExtra("Post_id");
        post_uid = getIntent().getStringExtra("Post_uid");

        uid = firebaseAuth.getCurrentUser().getUid();
        post_user_Info(post_uid);
        post_info(post_id);
        current_user_info(uid);

        comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentMsg = comment_message.getText().toString().trim();
                if ( commentMsg.length()>0){
                    if (post_uid != null){
                        up_comment(post_uid);
                        comment_message.setText("");
                    }
                }else {
                    Toast.makeText(CommentActivity.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Comments");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Comment comment = dataSnapshot1.getValue(Comment.class);
                    if (comment != null) {
                        if(post_id.equals(comment.getPost_id())) {
                            commentList.add(comment);
                        }
                    }
                }
                if (commentList.size() > 0){
                    CommentAdapter adapter = new CommentAdapter(commentList,CommentActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CommentActivity.this);
                    rv_comment_list.setLayoutManager(mLayoutManager);
                    rv_comment_list.setItemAnimator(new DefaultItemAnimator());
                    rv_comment_list.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void initView(){
        post_commentator_picture = findViewById(R.id.post_commentator_picture);
        tv_uer_name_comment_layout = findViewById(R.id.tv_uer_name_comment_layout);

        tv_post_text_comment_layout = findViewById(R.id.tv_post_text_comment_layout);
        tv_post_date_comment_layout = findViewById(R.id.tv_post_date_comment_layout);
        tv_post_time_comment_layout = findViewById(R.id.tv_post_time_comment_layout);

        iv_profile_pic_comment_layout = findViewById(R.id.iv_profile_pic_comment_layout);
        comment_message = findViewById(R.id.comment_message);
        comment_send = findViewById(R.id.comment_send);

        rv_comment_list = findViewById(R.id.rv_comment_list);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
          finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void post_user_Info(final String user_uid){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(user_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null){
                    tv_uer_name_comment_layout.setText(user.getName().toString());
                    Picasso.with(CommentActivity.this).load(user.getImage()).placeholder(R.drawable.user).into(post_commentator_picture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void post_info(final String post_fid){
        databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.child(post_fid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UploadPostModel post = dataSnapshot.getValue(UploadPostModel.class);
                if (post != null){
                    tv_post_text_comment_layout.setText(post.getPost_text().toString());
                    tv_post_date_comment_layout.setText(post.getPost_date().toString()+"   ");
                    tv_post_time_comment_layout.setText(post.getPost_time());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void current_user_info(String current_user_uid){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(current_user_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null){
                    tv_uer_name_comment_layout.setText(user.getName().toString());
                    Picasso.with(CommentActivity.this).load(user.getImage()).placeholder(R.drawable.user).into(iv_profile_pic_comment_layout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void up_comment(String post_uid){
        firebaseAuth = FirebaseAuth.getInstance();
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyy");
        String c_date = currentDate.format(calForDate.getTime());

        // pic up current time
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        String c_time = currentTime.format(calForTime.getTime());



        String cmt_msg = comment_message.getText().toString().trim();
        uid = firebaseAuth.getCurrentUser().getUid();
        String c_id = uid+post_uid+c_date+c_time;

        Comment comment = new Comment(c_id,cmt_msg,c_date,c_time,uid,post_uid,post_id);
        databaseReference = FirebaseDatabase.getInstance().getReference("Comments");
        databaseReference.child(c_id).setValue(comment);
    }
}
