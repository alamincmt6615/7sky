package com.alamin.healthcare.chatapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alamin.healthcare.chatapp.R;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;


public class SplashActivity extends AppCompatActivity {
    private static final int PIC_IMAGE_REQUEST = 420;
    private final String TAG = "CA/ProfileActivity";
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    Uri image_uri,cover_uri;

    private DatabaseReference userDatabase, requestsDatabase, friendsDatabase;
    private ValueEventListener userListener, requestsListener, friendsListerner;
    private String currentUserId;
    private TextView name, status;
    private CircleImageView image;
    private KenBurnsView cover;
    private FABsMenu menu;
    private TitleFAB button1, button2, button3;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        name = findViewById(R.id.profile_name);
        status = findViewById(R.id.profile_status);
        image = findViewById(R.id.profile_image);
        menu = findViewById(R.id.profile_fabs_menu);
        cover = findViewById(R.id.profile_cover);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initMyProfile();
    }
    public void initMyProfile() {
        if (button1 != null) {
            menu.removeButton(button1);
        }

        if (button2 != null) {
            menu.removeButton(button2);
        }

        if (button3 != null) {
            menu.removeButton(button3);
        }

        button1 = new TitleFAB(SplashActivity.this);
        button1.setTitle("Change Cover");
        button1.setBackgroundColor(getResources().getColor(R.color.colorPurple));
        button1.setRippleColor(getResources().getColor(R.color.colorPurpleDark));
        button1.setImageResource(R.drawable.ic_filter_hdr_white_24dp);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Cover"), 2);
                Toast.makeText(SplashActivity.this, "cover Photo clicked", Toast.LENGTH_SHORT).show();
                menu.collapse();
            }
        });
        menu.addButton(button1);
        button2 = new TitleFAB(SplashActivity.this);
        button2.setTitle("Change Image");
        button2.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        button2.setRippleColor(getResources().getColor(R.color.colorGreenDark));
        button2.setImageResource(R.drawable.ic_image_white_24dp);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select picture"), 1);
                Toast.makeText(SplashActivity.this, "profile pic clicked", Toast.LENGTH_SHORT).show();
                menu.collapse();
            }
        });
        menu.addButton(button2);
        button3 = new TitleFAB(SplashActivity.this);
        button3.setTitle("Change Status");
        button3.setBackgroundColor(getResources().getColor(R.color.colorBlue));
        button3.setRippleColor(getResources().getColor(R.color.colorBlueDark));
        button3.setImageResource(R.drawable.ic_edit_white_24dp);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SplashActivity.this, "profile Status clicked", Toast.LENGTH_SHORT).show();
                menu.collapse();
            }
        });
        menu.addButton(button3);
    }
    /*
    private void uploadimage() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("profile_pic");
        firebaseAuth = FirebaseAuth.getInstance();
        final String user_uid = firebaseAuth.getCurrentUser().getUid();

        if (pic_uri != null){
            progressDialog = new ProgressDialog(ProfileActivityCustomerEditable.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference ref = storageReference.child(user_uid+".jpg");
            ref.putFile(pic_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivityCustomerEditable.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.child("customer").child(user_uid).child("profile_pic_url").setValue( uri.toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivityCustomerEditable.this, "Failed "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading "+(int)progress+"%");
                        }
                    });
        }
    }

     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data!= null && data.getData() != null){
            cover_uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),cover_uri);
                cover.setImageBitmap(bitmap);
                if (bitmap != null){
                    Toast.makeText(this, "image selected", Toast.LENGTH_SHORT).show();
                   // uploadimage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == 1 && resultCode == RESULT_OK && data!= null && data.getData() != null){
            image_uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
                image.setImageBitmap(bitmap);
                if (bitmap != null){
                    Toast.makeText(this, "image selected", Toast.LENGTH_SHORT).show();
                     uploadimage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadcover(){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("cover_photo");
        firebaseAuth = FirebaseAuth.getInstance();
        if (cover_uri != null){
            final StorageReference abc = storageReference.child("ibIdxILelcVirtyr9WZhmWwipYE3cover"+".jgp");
            abc.putFile(cover_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    abc.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                        }
                    });
                }
            });
        }
    }
    private void uploadimage() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("profile_images");
        firebaseAuth = FirebaseAuth.getInstance();
        final String user_uid = firebaseAuth.getCurrentUser().getUid();


        if (image_uri != null){
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference ref = storageReference.child(user_uid+".jpg");
            ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressDialog.dismiss();
                    Toast.makeText(SplashActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.child(user_uid).child("image").setValue( uri.toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SplashActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading "+(int)progress+"%");
                        }
                    });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (menu.isExpanded()) {
            menu.collapse();
        } else {
            super.onBackPressed();
        }
    }
}