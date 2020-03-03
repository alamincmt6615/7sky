package com.alamin.healthcare.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;

import com.alamin.healthcare.chatapp.MainActivity;
import com.alamin.healthcare.chatapp.R;
import com.alamin.healthcare.chatapp.fragments.AboutUsFragment;
import com.alamin.healthcare.chatapp.fragments.ChatFragment;
import com.alamin.healthcare.chatapp.fragments.FriendsFragment;
import com.alamin.healthcare.chatapp.fragments.PostFragment;
import com.alamin.healthcare.chatapp.fragments.PublicPostFragment;
import com.alamin.healthcare.chatapp.fragments.RequestsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference databaseReference;
    private TextView nav_user_name;
    private ImageView nav_background;
    private CircularImageView nav_user_pic;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();


        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        nav_user_name = headerView.findViewById(R.id.nav_user_name);
        nav_user_pic = headerView.findViewById(R.id.nav_user_pic);
        nav_background = headerView.findViewById(R.id.nav_background);

        displaySelectedScreen(R.id.nav_public_post);

        if (firebaseUser != null) {
            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    nav_user_name.setText(dataSnapshot.child("name").getValue().toString());
                    Picasso.with(HomeActivity.this).load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.user).into(nav_user_pic);
                    Picasso.with(HomeActivity.this).load(dataSnapshot.child("cover").getValue().toString()).placeholder(R.drawable.healthcarenav).into(nav_background);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            Intent intent = new Intent(HomeActivity.this,WelcomeActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void onStart()
    {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            // If no logged in user send them to login/register

            Intent welcomeIntent = new Intent(HomeActivity.this, WelcomeActivity.class);
            startActivity(welcomeIntent);
            finish();
        }
    }

    private void displaySelectedScreen(int id) {
        Fragment fragment = null;
        Bundle bundle = null;
        switch (id) {
            case R.id.nav_home:
                fragment = new PostFragment();
                toast_maker("Private Post");
                break;
            case R.id.nav_public_post:
                fragment = new PublicPostFragment();
                break;
            case R.id.nav_friend:
                fragment = new FriendsFragment();
                toast_maker("Friends");
                break;
            case R.id.nav_friend_request:
                fragment = new RequestsFragment();
                toast_maker("Friend Request");
                break;
//            case R.id.nav_all_users:
//                nextActivity(FindFriendActivity.class);
//               // toast_maker("Users");
//                break;
            case R.id.nav_messages:
                fragment = new ChatFragment();
                toast_maker("Messages");
                break;
            case R.id.nav_profile:
                nextActivity(ProfileActivity.class);
                toast_maker("Profile");
                break;
            case R.id.nav_free_recharge:
                nextActivity(FreeRechargeActivity.class);
                toast_maker("Free Recharge");
                break;
            case R.id.nav_Chat_with_admin:
                nextActivity(ChatWithAdminActivity.class);
                toast_maker("Chat With Admin");
                break;
            case R.id.nav_about_us:
                fragment = new AboutUsFragment();
                toast_maker("this is nav_share");
                break;
                case R.id.nav_health_carePost:
                nextActivity(WebActivity.class);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                toast_maker("Logout");
                finish();
                break;
        }

        if(fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    private void nextActivity(Class selectedActivity) {
        Intent intent = new Intent(HomeActivity.this,selectedActivity);
        if (selectedActivity == ProfileActivity.class){
            intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
            finish();
        }else {
            startActivity(intent);
        }
    }
    private void toast_maker(String msg){
        Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        displaySelectedScreen(id);
        return true;
    }
}
