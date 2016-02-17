package com.ospring.o2lounge.fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.activities.MainActivity;
import com.ospring.o2lounge.activities.PhoneVerification;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vetero on 23-12-2015.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {

    EditText editTextName, editTextEmail, editTextPhone, editTextPassword;
    Button buttonSignUp;
    String name, email, password, phone;
    ProgressDialog pDialog;
    final String location = "http://www.oyasisspring.com/kashyapandriod/o2_server_files/sign_up.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, null, false);

        editTextName = (EditText) view.findViewById(R.id.et_name_sign_up);
        editTextEmail = (EditText) view.findViewById(R.id.et_email_sign_up);
        editTextPhone = (EditText) view.findViewById(R.id.et_phone_sign_up);
        editTextPassword = (EditText) view.findViewById(R.id.et_password_sign_up);

        buttonSignUp = (Button) view.findViewById(R.id.bt_sign_up_sign_up);

        buttonSignUp.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (onLine()) {
            name = editTextName.getText().toString();
            email = editTextEmail.getText().toString();
            phone = editTextPhone.getText().toString();
            password = editTextPassword.getText().toString();

            if (!isValidName(name)) {
                editTextName.setError("Invalid name!");
            } else if (!isValidEmail(email)) {
                editTextEmail.setError("Invalid email!");
            } else if (!isValidPassword(password)) {
                editTextPassword.setError("Enter valid password");
            } else if (!isValidPhone(phone)) {
                editTextPhone.setError("Enter valid phone");
            } else {
                try {
                    String urlParameters = URLEncoder.encode("name", "UTF-8")
                            + "=" + URLEncoder.encode(name, "UTF-8");

                    urlParameters += "&" + URLEncoder.encode("email", "UTF-8") + "="
                            + URLEncoder.encode(email, "UTF-8");

                    urlParameters += "&" + URLEncoder.encode("password", "UTF-8")
                            + "=" + URLEncoder.encode(password, "UTF-8");

                    urlParameters += "&" + URLEncoder.encode("phone", "UTF-8")
                            + "=" + URLEncoder.encode(phone, "UTF-8");

                    urlParameters += "&" + URLEncoder.encode("flag", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(false), "UTF-8");

                    new AddData().execute(urlParameters);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style
                    .MyAlertDialogStyle);
            builder.setTitle("Not Connected!");
            builder.setMessage("Please connect to internet");
            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Try again after connecting to internet",
                            Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }
    }

    private boolean onLine() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isValidPhone(String phone) {
        if (phone.length() != 10) {
            return false;
        } else {
            return Patterns.PHONE.matcher(phone).matches();
        }
    }

    private boolean isValidPassword(String password) {
        if (password != null && password.length() > 6) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidName(String name) {
        String regx = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }

    private class AddData extends AsyncTask implements DialogInterface.OnClickListener {
        String responseData, urlParameters;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpURLConnection connection = null;
            try {
                urlParameters = (String) params[0];
                URL url = new URL(location);
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
            pDialog.dismiss();
            if (responseData != null) {
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    int jInt = jObj.getInt("success");
                    if (jInt == 1) {
                        Toast.makeText(getContext(), "Successfully registered!", Toast.LENGTH_SHORT).show();
                        SharedPreferences sp = getContext().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context
                                .MODE_PRIVATE);
                        SharedPreferences.Editor et = sp.edit();

                        et.putString(Constants.KEY_SIGN_MAIL, email);
                        et.putBoolean(Constants.KEY_SIGNED_OR_NOT, true);
                        et.putString(Constants.SIGNED_CLIENT, "N");
                        et.putString(Constants.USER_NAME, name);
                        et.apply();

                        Intent intent = new Intent(getContext(), PhoneVerification.class);
                        intent.putExtra("Phone", phone);
                        startActivity(intent);
                    } else {
                        String jSrt = jObj.getString("message");
                        if (jSrt.equals("Email exists.")) {
                            Toast.makeText(getContext(), "User is registered, Please log in.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Some error occured!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getContext(), "Could not connect, please try later.", Toast
                        .LENGTH_SHORT).show();
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            final Intent emailLauncher = new Intent(Intent.ACTION_VIEW);
            emailLauncher.setType("message/rfc822");
            try {
                startActivity(emailLauncher);
            } catch (ActivityNotFoundException e) {
            }
        }
    }
}