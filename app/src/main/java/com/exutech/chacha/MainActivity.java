/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exutech.chacha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.exutech.chacha.adapters.FirebaseRecyclerAdapter;
import com.exutech.chacha.bean.FriendlyMessage;
import com.exutech.chacha.utils.LogUtil;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    public static String MESSAGES_CHILD = "chat/";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private RecyclerView mMessageRecyclerView2;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutManager2;
    private RecyclerView.Adapter mFirebaseAdapter;
//    private RecyclerView.Adapter mFirebaseAdapter2;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditText mMessageEditText;
    //    private AdView mAdView;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private GoogleApiClient mGoogleApiClient;
    private String mEmail;
    private String mFirebaseUid;
    private List<FriendlyMessage> datasFromSQL;

//    private  ExecutorService exec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            mEmail = mFirebaseUser.getEmail();
            mFirebaseUid = mFirebaseUser.getUid();
//            MESSAGES_CHILD += getClild(mFirebaseUid,"8732e51c-8bb1-43b6-831e-414ae297655c");
            MESSAGES_CHILD += getClild(mFirebaseUid, "m5IQAT6t1Bemt65SW4ZHgZKEodr1");

            LogUtil.getInstance().d(mFirebaseUid);
        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        datasFromSQL = new ArrayList<>();//getdatasFromSQL();
        mFirebaseAdapter = new FirebaseRecyclerAdapter(datasFromSQL, this,mFirebaseUser);//没网加载本地adaper
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);//本地数据

        mMessageRecyclerView2 = (RecyclerView) findViewById(R.id.messageRecyclerView2);
        mLinearLayoutManager2 = new LinearLayoutManager(this);
        FirebaseLisener.getInstance().onFirebaseLisener(mMessageRecyclerView2, mLinearLayoutManager2, mFirebaseDatabaseReference, MESSAGES_CHILD,new FirebaseLisener.CallbackFriendlyMessage() {
            @Override
            public void finish(FriendlyMessage friendlyMessage) {
                datasFromSQL.add(friendlyMessage);
//                mFirebaseAdapter.notifyDataSetChanged();
                mMessageRecyclerView.scrollToPosition(datasFromSQL.size() - 1);
                mFirebaseAdapter.notifyItemInserted(datasFromSQL.size()-1);
                insert(friendlyMessage);
            }
        });



   /*     // 广告
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        // 分析
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendlyMessage mFriendlyMessage = new FriendlyMessage();
                mFriendlyMessage.setName(mUsername);
                mFriendlyMessage.setUid(mFirebaseUid);
                mFriendlyMessage.setId(System.currentTimeMillis());
                mFriendlyMessage.setText(mMessageEditText.getText().toString());
                mFriendlyMessage.setPhotoUrl(mPhotoUrl);
                mFriendlyMessage.setEmail(mEmail);



                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(mFriendlyMessage);
                mMessageEditText.setText("");
                mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
//                datasFromSQL.add(mFriendlyMessage);
//                mFirebaseAdapter.notifyDataSetChanged();
//                mMessageRecyclerView.scrollToPosition(datasFromSQL.size() - 1);
//                insert(mFriendlyMessage);

            }
        });
//        exec = Executors.newSingleThreadExecutor();//单线程
    }




    private synchronized void insert(final FriendlyMessage friendlyMessage) {
//        exec.execute(new Runnable() {
//            @Override
//            public void run() {
//                getFriendlyMessageDao().insertOrReplace(friendlyMessage);
//            }
//        });
    }

//    private List<FriendlyMessage> getdatasFromSQL() {
//        return getFriendlyMessageDao().loadAll();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite_menu:
                sendInvitation();
                return true;
            case R.id.crash_menu:
                FirebaseCrash.logcat(Log.ERROR, TAG, "crash caused");
                causeCrash();
                return true;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.fresh_config_menu:
                fetchConfig();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void causeCrash() {
        throw new NullPointerException("Fake null pointer exception");
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config: " + e.getMessage());
                        applyRetrievedLengthLimit();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong("friendly_msg_length");
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, "FML is: " + friendly_msg_length);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    private String getClild(String uid, String uid2) {
        String[] arr = {uid.replace(".", ""), uid2.replace(".", "")};
        Arrays.sort(arr);
        return arr[0] + "&" + arr[1];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
