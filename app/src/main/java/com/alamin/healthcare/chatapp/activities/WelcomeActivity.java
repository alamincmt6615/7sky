package com.alamin.healthcare.chatapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alamin.healthcare.chatapp.MainActivity;
import com.alamin.healthcare.chatapp.R;
import com.alamin.healthcare.chatapp.utils.PreferenceData;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import top.wefor.circularanim.CircularAnim;

public class WelcomeActivity extends AppCompatActivity
{
    private final String TAG = "CA/WelcomeActivity";
    private EditText registration_otp;
    private TextView registerEmail;
    private String mVerificationId;
    private int alamin=0;
    ActionProcessButton registerButton;
    ActionProcessButton loginButton;
    FirebaseAuth mAuth;
    EditText registerName;
    EditText registerPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // activity_welcome views

        final EditText loginEmail = findViewById(R.id.login_email_text);
        final EditText loginPassword = findViewById(R.id.login_password_text);
        registerButton = findViewById(R.id.register_button);

        registration_otp = findViewById(R.id.registration_otp);

        registerName = findViewById(R.id.register_name_text);
        registerEmail = findViewById(R.id.register_email_text);
//        if(registerEmail.getText() != null && !registerEmail.getText().toString().equals("")){
//            registerEmail.setSelection(registerEmail.getText().length());
//        }
         registerPassword = findViewById(R.id.register_password_text);


        // Setting up login button work

        loginButton = findViewById(R.id.login_button);
        loginButton.setProgress(0);
        loginButton.setMode(ActionProcessButton.Mode.ENDLESS);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                loginButton.setClickable(false);

                loginEmail.clearFocus();
                loginPassword.clearFocus();

                // Hiding the soft keyboard

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginPassword.getWindowToken(), 0);

                if(loginEmail.getText().length() == 0 || loginPassword.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty.", Toast.LENGTH_SHORT).show();

                    loginButton.setProgress(-1);
                    loginButton.setClickable(true);
                }
                else
                {
                    loginButton.setProgress(1);

                    // Loggin user with data he gave us

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(loginEmail.getText().toString()+"@gmail.com", loginPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String token = FirebaseInstanceId.getInstance().getToken();
                                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                // Updating user device token

                                FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            loginButton.setProgress(100);

//                                            if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
//                                            {
                                                // Show animation and start activity

                                                new Handler().postDelayed(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        CircularAnim.fullActivity(WelcomeActivity.this, loginButton)
                                                                .colorOrImageRes(R.color.colorGreen)
                                                                .go(new CircularAnim.OnAnimationEndListener()
                                                                {
                                                                    @Override
                                                                    public void onAnimationEnd()
                                                                    {
                                                                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                                                        finish();
                                                                    }
                                                                });
                                                    }
                                                }, 1000);
//                                            }
//                                            else
//                                            {
//                                                Toast.makeText(getApplicationContext(), "Your email is not verified, we have sent you a new one.", Toast.LENGTH_LONG).show();
//                                                FirebaseAuth.getInstance().signOut();
//
//                                                loginButton.setProgress(-1);
//                                                loginButton.setClickable(true);
//                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "uploadToken failed: " + task.getException().getMessage());
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                Log.d(TAG, "signIn failed: " + task.getException().getMessage());

                                loginButton.setProgress(-1);
                                loginButton.setClickable(true);
                            }
                        }
                    });
                }
            }
        });

        // Will handle "enter key" login

        loginPassword.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    loginButton.performClick();
                    return true;
                }
                return false;
            }
        });
        registerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog(registerEmail.getText().toString());
                Toast.makeText(WelcomeActivity.this, "Enter Phone Number Here For Authentication ", Toast.LENGTH_SHORT).show();
            }
        });

        // Setting up register button

        registerButton.setProgress(0);
        registerButton.setMode(ActionProcessButton.Mode.ENDLESS);
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                registerButton.setClickable(false);

                loginButton.setClickable(false);

                registerName.clearFocus();
                registerEmail.clearFocus();
                registerPassword.clearFocus();


                // Hiding soft keyboard

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);


                if(registerEmail.getText().toString().length() == 0 || registerPassword.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty.", Toast.LENGTH_SHORT).show();

                    registerButton.setProgress(-1);
                    registerButton.setClickable(true);

                    loginButton.setClickable(true);
                }
                else
                {registerButton.setClickable(true);
                    if (alamin==1){
                        signInWithGmailAndPassword(registerEmail.getText().toString()+"@gmail.com",registerPassword.getText().toString());
                    }
                    // Registering user with data he gave us


                }
            }
        });

        // Will handle "enter key" register

        registerPassword.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    registerButton.performClick();
                    return true;
                }
                return false;
            }
        });

        // Will handle Terms and Condition text click

        final TextView registerTerms = findViewById(R.id.register_terms);
        registerTerms.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/alamincse6615")));
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.welcome_sliding);

        if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
        {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else
        {
            super.onBackPressed();
        }
    }
    private void OpenDialog(String nmbr) {
        final Dialog dialog = new Dialog(WelcomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.picup_phone_number);
        dialog.setCancelable(true);
        final EditText phone_number = dialog.findViewById(R.id.et_phone_number);
        phone_number.setText(nmbr);

        if(phone_number.getText() != null && !phone_number.getText().toString().equals("")){
            phone_number.setSelection(phone_number.getText().length());
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        phone_number.setInputType(InputType.TYPE_CLASS_NUMBER);
        phone_number.setHint("Enter phone number");
        phone_number.setMaxLines(3);
        phone_number.setMinLines(2);

        ((AppCompatButton) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phone_number.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)){
                    phone_number.setError("Empty");
                    phone_number.requestFocus();
                    return;
                }else {
                    sendVerificationCode(phoneNumber);
                    registerEmail.setText(phoneNumber);
                    dialog.dismiss();
                }


            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                registration_otp.setText(code);
                verifyVerificationCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };
    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(WelcomeActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    alamin=1;
                }

            }
        });
    }
    private void signInWithGmailAndPassword(String email, String password){
        registerButton.setProgress(1);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if(firebaseUser != null)
                    {
                        String userid = firebaseUser.getUid();

                        // "Packing" user data

                        Map map = new HashMap<>();
                        map.put("token", FirebaseInstanceId.getInstance().getToken());
                        map.put("name", registerName.getText().toString());
                        map.put("email", registerEmail.getText().toString());
                        map.put("status", "Welcome to my Profile!");
                        map.put("image", "default");
                        map.put("cover", "default");
                        map.put("date", ServerValue.TIMESTAMP);

                        // Uploading user data

                        FirebaseDatabase.getInstance().getReference().child("Users").child(userid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    registerButton.setProgress(100);

//                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
//                                    Toast.makeText(getApplicationContext(), "We have sent you a verification email to activate your account.", Toast.LENGTH_LONG).show();
//                                    FirebaseAuth.getInstance().signOut();
                                    loginButton.setClickable(true);
                                }
                                else
                                {
                                    Log.d(TAG, "registerData failed: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "createUser failed: " + task.getException().getMessage());
                    registerButton.setProgress(-1);
                    registerButton.setClickable(true);
                    loginButton.setClickable(true);
                }
            }
        });
    }
}
