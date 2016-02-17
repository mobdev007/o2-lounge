package com.ospring.o2lounge.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ospring.o2lounge.R;
import com.ospring.o2lounge.activities.MainActivity;
import com.ospring.o2lounge.activities.PhoneVerification;
import com.ospring.o2lounge.others.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Vetero on 13-01-2016.
 */
public class FacebookFragment extends Fragment {

    private CallbackManager callbackManager;
    ProfileTracker profileTracker;
    AccessTokenTracker accessTokenTracker;
    private FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
        private ProfileTracker mProfileTracker;

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (Profile.getCurrentProfile() == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        Log.v("facebook - profile", "facebook - profile 1 " + profile2.getFirstName
                                ());
                        Log.v("facebook - profile", "facebook - profile 1 " + profile2.getName());
                        Toast.makeText(getContext(), "Welcome, " + profile2.getName(), Toast
                                .LENGTH_SHORT).show();
                        mProfileTracker.stopTracking();
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants
                                .SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        boolean b = sharedPreferences.getBoolean(Constants.PHONE_VERIFIED_KEY,
                                false);
                        editor.putBoolean(Constants.KEY_SIGNED_OR_NOT, true);
                        editor.putString(Constants.SIGNED_CLIENT, "F");
                        editor.putString(Constants.USER_NAME, profile2.getName());
                        editor.putString(Constants.KEY_FACEBOOK_DP, String.valueOf(profile2
                                .getProfilePictureUri
                                        (50, 50)));
                        editor.apply();
                        if (!b) {
                            final EditText input = new EditText(getContext());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setTextColor(getResources().getColor(R.color.textColorPrimary));
                            input.setInputType(InputType.TYPE_CLASS_PHONE);
                            input.setGravity(Gravity.CENTER_HORIZONTAL);
                            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                            input.setLayoutParams(lp);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style
                                    .MyAlertDialogStyle)
                                    .setCancelable(false)
                                    .setMessage("Enter 10-digit Phone number")
                                    .setTitle("Verify contact number")
                                    .setView(input)
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences sharedPreferences1 =
                                                    getContext().getSharedPreferences(Constants
                                                            .SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                            editor1.putBoolean(Constants.PHONE_VERIFIED_KEY, false);
                                            editor1.apply();
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                            getActivity().finish();
                                        }
                                    })
                                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (isValidPhone(input.getText().toString())) {
                                                Intent intent = new Intent(getContext(), PhoneVerification
                                                        .class);
                                                intent.putExtra("Phone", input.getText().toString());
                                                startActivity(intent);
                                                getActivity().finish();
                                            } else {
                                                Toast.makeText(getContext(), "Invalid Phone number, you can confirm Later", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getContext(), MainActivity.class));
                                                getActivity().finish();
                                            }
                                        }
                                    });
                            builder.show();
                        }
                    }
                };
                mProfileTracker.startTracking();
                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        JSONObject json = response.getJSONObject();
                        try {
                            if (json != null) {
                                String text = json.getString("email");
                                SharedPreferences sharedPreferences = getContext()
                                        .getSharedPreferences(Constants.SHARED_PREFS_NAME,
                                                Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.KEY_SIGN_MAIL, text);
                                editor.apply();
                                Log.d("email", "1. JSON Facebook :: " + json);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("email", "1. JSON Facebook :: " + e.toString());
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            } else {
                Profile profile = Profile.getCurrentProfile();
                Log.v("facebook - profile", "facebook - profile 2 " + profile.getFirstName());
                Log.v("facebook - profile", "facebook - profile 2 " + profile.getName());
                Toast.makeText(getContext(), "Welcome, " + profile.getName(), Toast
                        .LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants
                        .SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean b = sharedPreferences.getBoolean(Constants.PHONE_VERIFIED_KEY, false);
                editor.putBoolean(Constants.KEY_SIGNED_OR_NOT, true);
                editor.putString(Constants.SIGNED_CLIENT, "F");
                editor.putString(Constants.USER_NAME, profile.getName());
                editor.putString(Constants.KEY_FACEBOOK_DP, String.valueOf(profile
                        .getProfilePictureUri
                                (50, 50)));
                editor.apply();
                if (!b) {
                    final EditText input = new EditText(getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setTextColor(getResources().getColor(R.color.textColorPrimary));
                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                    input.setGravity(Gravity.CENTER_HORIZONTAL);
                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    input.setLayoutParams(lp);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style
                            .MyAlertDialogStyle)
                            .setCancelable(false)
                            .setMessage("Enter 10-digit Phone number")
                            .setTitle("Verify contact number")
                            .setView(input)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences sharedPreferences1 =
                                            getContext().getSharedPreferences(Constants
                                                    .SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                    editor1.putBoolean(Constants.PHONE_VERIFIED_KEY, false);
                                    editor1.apply();
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    getActivity().finish();
                                }
                            })
                            .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isValidPhone(input.getText().toString())) {
                                        Intent intent = new Intent(getContext(), PhoneVerification
                                                .class);
                                        intent.putExtra("Phone", input.getText().toString());
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(getContext(), "Invalid Phone number, you can confirm Later", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(), MainActivity.class));
                                        getActivity().finish();
                                    }
                                }
                            });
                    builder.show();
                }
                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        JSONObject json = response.getJSONObject();
                        try {
                            if (json != null) {
                                String text = json.getString("email");
                                SharedPreferences sharedPreferences = getContext()
                                        .getSharedPreferences(Constants.SHARED_PREFS_NAME,
                                                Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.KEY_SIGN_MAIL, text);
                                editor.apply();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("email", "2. JSON Facebook :: " + e.toString());
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    private boolean isValidPhone(String phone) {
        return phone.length() == 10 && Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.facebook_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, loginResultFacebookCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}