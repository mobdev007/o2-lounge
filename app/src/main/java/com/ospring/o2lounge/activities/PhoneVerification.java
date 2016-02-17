package com.ospring.o2lounge.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.others.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Vetero on 21-01-2016.
 */
public class PhoneVerification extends AppCompatActivity implements View.OnClickListener {

    Button button, button1;
    ProgressDialog progressDialog;
    final String locationSMS = "http://www.oyasisspring" +
            ".com/kashyapandriod/o2_server_files/request_sms.php";
    final String locationOTP = "http://www.oyasisspring.com/kashyapandriod/o2_server_files/verify_otp.php";
    EditText editText;
    private static final String TAG = PhoneVerification.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String s = getIntent().getStringExtra("Phone");
        setContentView(R.layout.actvivity_phone_verification);

        button = (Button) findViewById(R.id.btn_cancel_otp);
        button1 = (Button) findViewById(R.id.btn_verify_otp);
        editText = (EditText) findViewById(R.id.inputOtp);

        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants
                    .SHARED_PREFS_NAME, MODE_PRIVATE);
            String name = sharedPreferences.getString(Constants.USER_NAME, null);
            String email = sharedPreferences.getString(Constants.KEY_SIGN_MAIL, null);
            String urlParameters = URLEncoder.encode("mobile", "UTF-8") + "="
                    + URLEncoder.encode(s, "UTF-8");

            urlParameters += "&" + URLEncoder.encode("email", "UTF-8")
                    + "=" + URLEncoder.encode(email, "UTF-8");

            urlParameters += "&" + URLEncoder.encode("name", "UTF-8")
                    + "=" + URLEncoder.encode(name, "UTF-8");
            new VerifyPhone().execute(urlParameters);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_otp:
                SharedPreferences sharedPreferences = getSharedPreferences(Constants
                        .SHARED_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.PHONE_VERIFIED_KEY, false);
                editor.apply();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;

            case R.id.btn_verify_otp:
                String s = editText.getText().toString();
                if (s.length() == 6) {
                    String urlParameters;
                    try {
                        SharedPreferences sharedPreferences1 = getSharedPreferences(Constants
                                .SHARED_PREFS_NAME, MODE_PRIVATE);
                        String s1 = sharedPreferences1.getString(Constants.KEY_SIGN_MAIL, null);
                        urlParameters = URLEncoder.encode("otp", "UTF-8") + "="
                                + URLEncoder.encode(s, "UTF-8");

                        urlParameters += "&" + URLEncoder.encode("email", "UTF-8")
                                + "=" + URLEncoder.encode(s1, "UTF-8");

                        new VerifyOTP().execute(urlParameters);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    editText.setError("Invalid OTP");
                }
                break;

            default:
                break;
        }
    }

    private class VerifyPhone extends AsyncTask {

        String urlParameters, responseData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PhoneVerification.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Sending SMS to your number...");
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpURLConnection connection = null;
            try {
                urlParameters = (String) params[0];
                URL url = new URL(locationSMS);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                responseData = response.toString();
                return responseData;

            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
            if (responseData != null) {
                try {
                    Log.d("RESPONSE", "Response JSON :: " + responseData);
                    JSONObject jObj = new JSONObject(responseData);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(PhoneVerification.this, "Detecting SMS sent to your number.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PhoneVerification.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PhoneVerification.this, "Could not connect, please try later.", Toast
                        .LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class VerifyOTP extends AsyncTask {

        String urlParameters, responseData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PhoneVerification.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Verifying OTP..");
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpURLConnection connection = null;
            try {
                urlParameters = (String) params[0];
                URL url = new URL(locationOTP);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                responseData = response.toString();
                return responseData;

            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
            if (responseData != null) {
                Log.e(TAG, "OTP Response: " + responseData);
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    boolean error = jsonObject.getBoolean("error");
                    String message = jsonObject.getString("message");
                    if (!error) {
                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants
                                .SHARED_PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constants.PHONE_VERIFIED_KEY, true);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PhoneVerification.this, "Could not connect, please try later.", Toast
                        .LENGTH_SHORT)
                        .show();
            }
        }
    }
}