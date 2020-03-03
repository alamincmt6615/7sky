package com.alamin.healthcare.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alamin.healthcare.chatapp.MainActivity;
import com.alamin.healthcare.chatapp.R;
import com.alamin.healthcare.chatapp.adapters.RetreivePersonalPost;
import com.alamin.healthcare.chatapp.adapters.RetrievePublicPost;
import com.alamin.healthcare.chatapp.models.UploadPostModel;
import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class PostFragment extends Fragment {
    TextView et_post_text;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferencePost;
    private FirebaseAuth mAuth;
    private RetrievePublicPost retrievePublicPost;
    private RecyclerView rv_profile_fragment;
    private RetreivePersonalPost retreivePersonalPost;
    private Button post_btn;
    private String uid;
    String name,profile_pic_url;
    private ArrayList<UploadPostModel> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        databaseReferencePost = FirebaseDatabase.getInstance().getReference("Users");
        et_post_text = root.findViewById(R.id.et_post_text);
        rv_profile_fragment = root.findViewById(R.id.rv_profile_fragment);
        post_btn = root.findViewById(R.id.post_btn);
        mAuth = FirebaseAuth.getInstance();

        uid = mAuth.getCurrentUser().getUid();
        user_post_retrieve(uid);

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_post(uid);
            }
        });
        return root;
    }
    private void user_post(final String uid) {
        //pic up current date
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        // pic up current time
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        final String random_key = uid + saveCurrentDate + saveCurrentTime;

        final String post_text = et_post_text.getText().toString().trim();


        if (TextUtils.isEmpty(post_text)){
            Toast.makeText(getActivity(), "Write your post first", Toast.LENGTH_SHORT).show();
            return;
        }else {

            databaseReferencePost.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.child("name").getValue().toString();
                    profile_pic_url = dataSnapshot.child("image").getValue().toString();

                    UploadPostModel uploadPostModel = new UploadPostModel(name,profile_pic_url,random_key,post_text,uid,saveCurrentTime,saveCurrentDate);
                    databaseReference.child(random_key).setValue(uploadPostModel);
                    et_post_text.setText("");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
    private void user_post_retrieve(final String uid){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    UploadPostModel uploadPostModel = dataSnapshot1.getValue(UploadPostModel.class);
                    if (uid.equals(uploadPostModel.getUser_uid())){
                        list.add(uploadPostModel);
                        String nam = uploadPostModel.getPost_text();
                        Log.d("text",nam);
                    }
                }
                if (list.size()>0){
                    retreivePersonalPost = new RetreivePersonalPost(list,getActivity());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rv_profile_fragment.setLayoutManager(mLayoutManager);
                    rv_profile_fragment.setItemAnimator(new DefaultItemAnimator());
                    rv_profile_fragment.setAdapter(retreivePersonalPost);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}