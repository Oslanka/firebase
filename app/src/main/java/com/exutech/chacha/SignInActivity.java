/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exutech.chacha;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private SignInButton mSignInButton;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(com.exutech.chacha.R.layout.activity_sign_in);

        // Assign fields
        mSignInButton = (SignInButton) findViewById(com.exutech.chacha.R.id.sign_in_button);

        // Set click listeners
        mSignInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.exutech.chacha.R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();


        // Initialize Facebook Login button
        CallbackManager.Factory.create();

        mCallbackManager = CallbackManager.Factory.create();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        final LoginButton loginButton = (LoginButton) findViewById(com.exutech.chacha.R.id.loginfacebook);
        loginButton.setReadPermissions("email", "public_profile","user_status","user_birthday","user_friends","user_location","user_hometown");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                String userId = loginResult.getAccessToken().getUserId();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");
                                    String birthday = object.getString("birthday"); // 01/31/1980 format
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                // 字段参考  https://developers.facebook.com/docs/graph-api/reference/user
                parameters.putString("fields", "id,name,picture,email,gender,birthday,first_name,last_name,location,locale,timezone,updated_time,verified,hometown,cover");
                request.setParameters(parameters);
                request.executeAsync();
//                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("info", error.toString());
            }
        });
        //// TODO: 16/5/31 邮箱登录
        final EditText username = (EditText) findViewById(com.exutech.chacha.R.id.email_editext);
        final EditText pass = (EditText) findViewById(com.exutech.chacha.R.id.email_pass);

        findViewById(com.exutech.chacha.R.id.email_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin(username, pass);
//                mAuth.signInWithCustomToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOiIyIiwiaWF0IjoxNDY1MTc3NDU2LCJleHAiOjE0NjUxODEwNTYsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwiaXNzIjoiY2hhY2hhQHByb2plY3QtNTI3MDI5Mjc3NTAzODkwODU5NC5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsInN1YiI6ImNoYWNoYUBwcm9qZWN0LTUyNzAyOTI3NzUwMzg5MDg1OTQuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20ifQ.H6sB1_PyLw4G2aNMUlvhNS-QkExJt_h09FwbMoUDrZ7tVIdvQXOXswHkdClpRSPKM8aqXMc2-sXc3gwvSFhzlKuAcZc1ja85MJbitU7TqkEkCkYBhJdMNjv4xJXgw6NJ9Y2JjM9ojTyN8uLKOos0orZn0W7jBYgqTIJ61Uw4_d2aYrmy9HPEtzkp4pU4jYgR5WO8-QK9AIemrtZ5wCUN13sbfOlXYrh3IW99wROdG7HA1PR7gcZFEZC7ZaOw-6amH-ZgekREohfgzdVuFLZkUCxZScnkeVxPebdbIlRmaPesMBR2NhU3a8bnQeg_Lb68jIILfAvs7qe2lVyVOgdAuA")
//                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                Log.d(TAG, "signInWithCustomToken:onComplete:" + task.isSuccessful());
//
//                                // If sign in fails, display a message to the user. If sign in succeeds
//                                // the auth state listener will be notified and logic to handle the
//                                // signed in user can be handled in the listener.
//                                loginOrNot(task);
//                            }
//                        });

            }
        });




    }

    private void emailLogin(EditText username, EditText pass) {
        handleEmialToken(username.getText().toString(),pass.getText().toString());
    }

    private void handleFirebaseAuthResult(AuthResult authResult) {
        if (authResult != null) {
            // Welcome the user
            FirebaseUser user = authResult.getUser();
            Toast.makeText(this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

            // Go back to the main activity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.exutech.chacha.R.id.sign_in_button:
                signIn();
                break;
            default:
                return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        loginOrNot(task);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        AuthCredential credential = FacebookAuthProvider.getCredential("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJjaGFjaGFAcHJvamVjdC01MjcwMjkyNzc1MDM4OTA4NTk0LmlhbS5nc2VydmljZWFjY291bnQuY29tIiwic3ViIjoiY2hhY2hhQHByb2plY3QtNTI3MDI5Mjc3NTAzODkwODU5NC5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsImF1ZCI6Imh0dHBzOlwvXC9pZGVudGl0eXRvb2xraXQuZ29vZ2xlYXBpcy5jb21cL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwiaWF0IjoxNDY0OTQzMjIxLCJleHAiOjE0NjQ5NDY4MjEsInVpZCI6MTIzLCJjbGFpbXMiOnsicHJlbWl1bV9hY2NvdW50IjpmYWxzZX19.skqyYybtPbRvpDuOHhflO1Vei3XPXmbhtPdF4qhIXkk");

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        loginOrNot(task);
                    }
                });
    }
    private void handleEmialToken(String email ,String password ) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "linkWithCredential:onComplete:" + task.isSuccessful());
                        loginOrNot(task);
                    }
                });
    }

    private void loginOrNot(@NonNull Task<AuthResult> task) {
        if (!task.isSuccessful()) {
            Exception exception = task.getException();
            Log.w(TAG, "signInWithCredential", exception);

            Toast.makeText(SignInActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }
}
