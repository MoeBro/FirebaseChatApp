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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

        private SignInButton GoogleSignIn;
        private LoginButton facebookButton;
        private static int RC_SIGN_IN = 9001;
        private CallbackManager callbackManager;
        AuthStateListener mAuthListener;
        FirebaseAuth mAuth;
        private GoogleApiClient mGoogleApiClient;
        private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        facebookButton = (LoginButton)findViewById(R.id.facebookButton);
        GoogleSignIn = (SignInButton)findViewById(R.id.google_loginBtn);
        GoogleSignIn.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("91228041707-fbnlqhef5vsa6dsh3fqc0ufkmfb6ogub.apps.googleusercontent.com")
                .requestEmail()
                .build();
        final Intent intent = new Intent(this,ChatRoomList.class);

        //handling results from facebook login
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //show successfull log in message and switch to chat room list activity
                Toast.makeText(LogInActivity.this,"Succesfully logged in",Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
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

        mAuthListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    Intent intent = new Intent(LogInActivity.this,ChatRoomList.class);
                    startActivity(intent);
                }
            }
        };

        if (AccessToken.getCurrentAccessToken()!=null){
            startActivity(intent);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LogInActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(true);
                        } else {
                            // If sign in fails, display a message to the user.

                           ;
                            //updateUI(false);
                        }

                        // ...
                    }
                });
    }

    //Handling clicks on login buttons
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.google_loginBtn:
                signIn();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Opening Google Log In activity
    private void signIn() {
        System.out.println("hejhejehej");
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent,RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            System.out.println("test2");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                System.out.println("test1");
                GoogleSignInAccount account = result.getSignInAccount();
                authWithGoogle(account);
            }
        }
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void authWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    System.out.println("hawewae");
                    startActivity(new Intent(getApplicationContext(),ChatRoomList.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Auth Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Getting Users name from facebook
    private void getFacebookUsername (AccessToken newAccessToken)
    {
        
    }

}
