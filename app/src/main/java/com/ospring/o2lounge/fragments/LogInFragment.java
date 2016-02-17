package com.ospring.o2lounge.fragments;

import android.app.ProgressDialog;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.activities.MainActivity;
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
public class LogInFragment extends Fragment implements View.OnClickListener {

    Button buttonShowPassword, buttonLogIn, buttonSignUp, buttonSkip;
    EditText editTextPassword, editTextEmail;
    String email,password;
    ProgressDialog progressDialog;
    final String location = "http://www.oyasisspring.com/kashyapandriod/o2_server_files/sign_in.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, null, false);


        editTextPassword = (EditText) view.findViewById(R.id.et_password_log_in);
        editTextEmail = (EditText) view.findViewById(R.id.et_email_log_in);

        buttonShowPassword = (Button) view.findViewById(R.id.bt_log_in_show_pass);
        buttonLogIn = (Button) view.findViewById(R.id.bt_log_in);
        buttonSignUp = (Button) view.findViewById(R.id.bt_sign_up);
        buttonSkip = (Button) view.findViewById(R.id.bt_log_in_frag_skip);

        buttonShowPassword.setOnClickListener(this);
        buttonLogIn.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);
        buttonSkip.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_log_in_show_pass:
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                editTextPassword.setSelection(editTextPassword.getText().length());
                break;

            case R.id.bt_log_in:
                if(onLine()) {
                    email = editTextEmail.getText().toString();
                    if (!isValidEmail(email)) {
                        editTextEmail.setError("Invalid Email");
                    }

                    password = editTextPassword.getText().toString();
                    if (!isValidPassword(password)) {
                        editTextPassword.setError("Invalid Password");
                    }
                    if (isValidPassword(password) && (isValidEmail(email))) {
                        try {
                            String urlParameters = URLEncoder.encode("email", "UTF-8") + "="
                                    + URLEncoder.encode(email, "UTF-8");

                            urlParameters += "&" + URLEncoder.encode("password", "UTF-8")
                                    + "=" + URLEncoder.encode(password, "UTF-8");

                            new VerifyUser().execute(urlParameters);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
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
                break;

            case R.id.bt_sign_up:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout_log_in, new SignUpFragment());
                fragmentTransaction.commit();
                break;

            case R.id.bt_log_in_frag_skip:
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants
                        .SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.KEY_SKIP_FLAG, true);
                editor.apply();
                startActivity(new Intent(getContext(), MainActivity.class));
        }
    }

    private boolean onLine() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
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

    private class VerifyUser extends AsyncTask {
        String urlParameters, responseData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
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
            progressDialog.dismiss();
            if (responseData != null) {
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    int jInt = jObj.getInt("success");
                    if (jInt == 1) {
                        SharedPreferences sp = getContext().getSharedPreferences(Constants
                                        .SHARED_PREFS_NAME,
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor et = sp.edit();
                        et.putString(Constants.KEY_SIGN_MAIL, email);
                        et.putBoolean(Constants.KEY_SIGNED_OR_NOT, true);
                        et.commit();
                        Toast.makeText(getContext(), "Log in success!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                    } else {
                        String message = jObj.getString("message");
                        if (message.equals("Incorrect password.")) {
                            Toast.makeText(getContext(), "Password does not match", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(getContext(), "Email does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getContext(), "Could not connect, please try later.", Toast.LENGTH_SHORT).show();
            }

        }
    }
}