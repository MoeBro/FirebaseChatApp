package com.example.firebasechat.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.firebasechat.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


public class LogInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

        private SignInButton GoogleSignIn;
        private LoginButton facebookButton;
        private GoogleApiClient googleApiClient;
        private static final int REQ_CODE = 9001;
        private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        facebookButton = (LoginButton)findViewById(R.id.facebookButton);
        GoogleSignIn = (SignInButton)findViewById(R.id.google_loginBtn);
        GoogleSignIn.setOnClickListener(this);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();
        final Intent intent = new Intent(this,ChatRoomList.class);

        //handling results from facebook login
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //show successfull log in message and switch to chat room list activity
                Toast.makeText(LogInActivity.this,"Succesfully logged in",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Toast.makeText(LogInActivity.this,"Log in cancelled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LogInActivity.this,"An error occurred, try again ",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Handling clicks on login buttons
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.google_loginBtn:
                googleSignIn();
                break;

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    //Opening Google Log In activity
    public void googleSignIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);

    }

    // Handling results of Google Log In Activity
    public void handleGoogleSignInResult(GoogleSignInResult result)
    {
        if(result.isSuccess())
        {
            updateUI(true);
        }
        else
        {
            Toast.makeText(LogInActivity.this,"An error occurred, try again",Toast.LENGTH_SHORT).show();
        }

    }
    //Move to chatroom activity when sign in successful
    public void updateUI(boolean isLoggedIn)
    {
        Intent intent = new Intent(this,ChatRoomList.class);
        if(isLoggedIn)
        {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }

        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    //Getting Users name from facebook login
    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

        }
    };
    //Getting Users name from facebook
    private void getFacebookUsername (AccessToken newAccessToken)
    {

    }
}
