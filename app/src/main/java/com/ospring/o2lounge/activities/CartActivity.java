package com.ospring.o2lounge.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.fragments.CartFragment;
import com.ospring.o2lounge.fragments.PhoneNumberVerfFragment;
import com.ospring.o2lounge.others.Constants;
import com.ospring.o2lounge.others.DBAdapter;
import com.ospring.o2lounge.others.OrderHistoryDb;
import com.parse.ParseInstallation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Vetero on 20-12-2015.
 */
public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG = CartActivity.class.getSimpleName();
    Toolbar toolbar;
    TextView textView;
    Button button;
    DBAdapter myDb;
    OrderHistoryDb myOrderHistoryDb;
    ProgressDialog progressDialog;
    final String location = "http://www.oyasisspring.com/kashyapandriod/o2_server_files/order.php";
    static String order_day;
    static String order_time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = (Toolbar) findViewById(R.id.toolbar_cart);
        toolbar.setTitle("Cart");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.textView = (TextView) findViewById(R.id.tv_cart_grand_total);
        button = (Button) findViewById(R.id.bt_cart_check_out);

        textView.append(getGrandTotal());
        button.setOnClickListener(this);

        addCartFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public CharSequence getGrandTotal() {
        openDb();
        String s = myDb.getGrandTotal();
        closeDb();
        return s;
    }

    private void closeDb() {
        myDb.close();
    }

    private void openDb() {
        myDb = new DBAdapter(this);
        myDb.open();
    }

    private void addCartFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout_cart, new CartFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        if (Integer.parseInt(String.valueOf(getGrandTotal())) == 0) {
            Toast.makeText(CartActivity.this, "Please select atleast one item to check out", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_NAME,
                    MODE_PRIVATE);
            boolean b = sharedPreferences.getBoolean(Constants.KEY_SIGNED_OR_NOT, false);
            if (!b) {
                startActivity(new Intent(this, SignUpLog.class));
            } else {
                boolean b1 = sharedPreferences.getBoolean(Constants.PHONE_VERIFIED_KEY, false);
                if (!b1) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.popBackStack();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_layout_cart, new
                            PhoneNumberVerfFragment());
                    fragmentTransaction.commit();
                    toolbar.setTitle("Verify Number");
                    toolbar.setTitleTextColor(0xFFFFFFFF);
                    textView.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                } else {
                    confirmOrder();
                }
            }
        }
    }

    public void confirmOrder() {
        if (onLine()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setTitle("Confirm?");
            builder.setMessage("Are you sure to confirm?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final CharSequence charSequence[] = {"Today", "Tomorrow", "Day " +
                            "after tomorrow"};
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(CartActivity.this, R
                            .style
                            .MyAlertDialogStyle);
                    builder1.setTitle("Select Day");
                    builder1.setSingleChoiceItems(charSequence, 0, new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            order_day = String.valueOf(charSequence[which]);
                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(CartActivity.this,
                                    new
                                            TimePickerDialog
                                                    .OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                                    order_time = selectedHour + ":" + selectedMinute;
                                                    new sendOrder().execute();
                                                }
                                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Select Time");
                            mTimePicker.show();
                        }
                    });
                    builder1.setCancelable(false);
                    builder1.show();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                }
            });
            builder.setCancelable(false);
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style
                    .MyAlertDialogStyle);
            builder.setTitle("Not Connected!");
            builder.setMessage("Please connect to internet");
            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(CartActivity.this, "Try again after connecting to internet",
                            Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }
    }

    private boolean onLine() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class sendOrder extends AsyncTask {

        String st, responseData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CartActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Placing your order...");
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            openDb();
            Cursor cursor = myDb.getAllRows();
            closeDb();
            JSONObject jobj;
            JSONArray arr = new JSONArray();
            cursor.moveToFirst();
            do {
                jobj = new JSONObject();
                try {
                    jobj.put("Name", cursor.getString(1));
                    jobj.put("Price", cursor.getString(2));
                    jobj.put("Quantity", cursor.getString(3));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arr.put(jobj);
            } while (cursor.moveToNext());

            st = arr.toString();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_NAME,
                    MODE_PRIVATE);
            String email = sharedPreferences.getString(Constants.KEY_SIGN_MAIL, "No email");
            String urlParameters = null;
            HttpURLConnection connection = null;
            try {
                String id = ParseInstallation.getCurrentInstallation().getObjectId();
                urlParameters = URLEncoder.encode("email", "UTF-8") + "="
                        + URLEncoder.encode(email, "UTF-8");

                urlParameters += "&" + URLEncoder.encode("order", "UTF-8")
                        + "=" + URLEncoder.encode(st, "UTF-8");

                urlParameters += "&" + URLEncoder.encode("time_for_order", "UTF-8") + "=" +
                        URLEncoder.encode(order_time, "UTF-8");

                urlParameters += "&" + URLEncoder.encode("day_for_order", "UTF-8") + "=" +
                        URLEncoder.encode(order_day, "UTF-8");

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
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseData;
        }

        @Override
        protected void onPostExecute(Object o) {
            progressDialog.dismiss();
            if (responseData != null) {
                Log.i(LOG, "Order ::" + responseData);
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    boolean b = jsonObject.getBoolean("result");
                    if (b){
                        Toast.makeText(CartActivity.this, "We will contact you 30 minutes before your arrival", Toast.LENGTH_SHORT).show();
                        List<String> names;
                        openDb();
                        names = myDb.getOrderItems();
                        String i = myDb.getGrandTotal();
                        myDb.deleteAll();
                        closeDb();
                        for (int i1 = 0; i1 < names.size(); i1++) {
                            Log.i("Order Items", String.valueOf(names.get(i1)));
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        myOrderHistoryDb = new OrderHistoryDb(getApplicationContext());
                        myOrderHistoryDb.open();
                        long l = myOrderHistoryDb.insertRow(names, currentDateandTime, Integer.parseInt(i));
                        myOrderHistoryDb.close();
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    }
                    else {
                        Toast.makeText(CartActivity.this, "Sorry, could not place order", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CartActivity.this, "Could not connect, please try later.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}