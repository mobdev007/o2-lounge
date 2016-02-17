package com.ospring.o2lounge.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ospring.o2lounge.R;
import com.ospring.o2lounge.fragments.FacebookFragment;
import com.ospring.o2lounge.fragments.LogInFragment;
import com.ospring.o2lounge.others.Constants;

/**
 * Created by Vetero on 21-12-2015.
 */
public class SignUpLog extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        startFragmnetLogIn();
        startFaebookFragment();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);
    }

    private void startFaebookFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.facebook_fragment, new FacebookFragment());
        fragmentTransaction.commit();
    }

    private void startFragmnetLogIn() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_layout_log_in, new LogInFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
//        SharedPreferences sharedPreferences = getSharedPreferences(Constants
//                .SHARED_PREFS_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean(Constants.KEY_SKIP_FLAG, true);
//        editor.apply();
//        startActivity(new Intent(this,MainActivity.class));
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(SignUpLog.this, acct.getDisplayName(), Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants
                    .SHARED_PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean b = sharedPreferences.getBoolean(Constants.PHONE_VERIFIED_KEY, false);
            editor.putString(Constants.KEY_SIGN_MAIL, acct.getEmail());
            editor.putBoolean(Constants.KEY_SIGNED_OR_NOT, true);
            editor.putString(Constants.SIGNED_CLIENT, "G");
            editor.putString(Constants.USER_NAME, acct.getDisplayName());
            editor.putString(Constants.KEY_GMAIL_DP, String.valueOf(acct.getPhotoUrl()));
            editor.apply();
            Log.i("Google", "Google JSON ::" + acct.toString());
            if(!b) {
                final EditText input = new EditText(SignUpLog.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setGravity(Gravity.CENTER_HORIZONTAL);
                input.setTextColor(getResources().getColor(R.color.textColorPrimary));
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                input.setLayoutParams(lp);
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                        .setCancelable(false)
                        .setMessage("Enter 10-digit Phone number")
                        .setTitle("Verify contact number")
                        .setView(input)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPreferences1 = getSharedPreferences(Constants
                                        .SHARED_PREFS_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                editor1.putBoolean(Constants.PHONE_VERIFIED_KEY, false);
                                editor1.apply();
                                startActivity(new Intent(SignUpLog.this, MainActivity.class));
                                finish();
                            }
                        })
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isValidPhone(input.getText().toString())) {
                                    Intent intent = new Intent(SignUpLog.this, PhoneVerification.class);
                                    intent.putExtra("Phone", input.getText().toString());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpLog.this, "Phone number not valid, you can " +
                                            "confirm Later", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpLog.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
                builder.show();
            }
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private boolean isValidPhone(String phone) {
        return phone.length() == 10 && Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(SignUpLog.this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }
}