package com.ospring.o2lounge.others;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.ospring.o2lounge.activities.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpService extends IntentService {

    String responseData;
    private static final String TAG = HttpService.class.getSimpleName();
    final String location = "http://www.oyasisspring.com/kashyapandriod/o2_server_files/verify_otp.php";

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");

            SharedPreferences sharedPreferences = getSharedPreferences(Constants
                    .SHARED_PREFS_NAME, MODE_PRIVATE);
            String email = sharedPreferences.getString(Constants.KEY_SIGN_MAIL, "");
            String urlParameters;
            try {
                urlParameters = URLEncoder.encode("otp", "UTF-8")
                        + "=" + URLEncoder.encode(otp, "UTF-8");

                urlParameters += "&" + URLEncoder.encode("email", "UTF-8") + "="
                        + URLEncoder.encode(email, "UTF-8");

                HttpURLConnection connection = null;
                try {
                    Log.e(TAG, "Received SMS resp: " + "connection started");
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
                    StringBuilder response = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    responseData = response.toString();
                    Log.e(TAG, "Received SMS resp: " + responseData);

                    JSONObject jsonObject = new JSONObject(responseData);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error){
                        Intent intent1 = new Intent(HttpService.this, MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        SharedPreferences sharedPreferences1 = getSharedPreferences(Constants
                                .SHARED_PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                        editor.putBoolean(Constants.PHONE_VERIFIED_KEY, true);
                        editor.apply();
                        stopSelf();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}