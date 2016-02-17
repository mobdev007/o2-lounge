package com.ospring.o2lounge.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ospring.o2lounge.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Vetero on 12-12-2015.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    ImageButton ibAppetizer, ibBeverages, ibRice, ibHookah;
    ViewPager viewPager;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null, false);

        ibAppetizer = (ImageButton) v.findViewById(R.id.ib_appetizer_home_frag);
        ibBeverages = (ImageButton) v.findViewById(R.id.ib_beverages_home_frag);
        ibRice = (ImageButton) v.findViewById(R.id.ib_rice_items_home_frag);
        ibHookah = (ImageButton) v.findViewById(R.id.ib_hookah_home_frag);

        if (online()) {
            new GetOffers().execute();
        } else {
            Toast.makeText(getContext(), "Connect to internet for Special Offers", Toast.LENGTH_SHORT).show();
        }

        viewPager = (ViewPager) v.findViewById(R.id.viewpager_home_frag);

        ibAppetizer.setOnClickListener(this);
        ibBeverages.setOnClickListener(this);
        ibRice.setOnClickListener(this);
        ibHookah.setOnClickListener(this);
        return v;
    }

    private boolean online() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_appetizer_home_frag:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new FoodFragment());
                fragmentTransaction.commit();
                break;

            case R.id.ib_beverages_home_frag:
                FragmentManager fragmentManager1 = getFragmentManager();
                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                fragmentTransaction1.replace(R.id.fragment_layout, new FoodFragment());
                fragmentTransaction1.commit();
                break;

            case R.id.ib_rice_items_home_frag:
                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                fragmentTransaction2.replace(R.id.fragment_layout, new FoodFragment());
                fragmentTransaction2.commit();
                break;

            case R.id.ib_hookah_home_frag:
                FragmentManager fragmentManager3 = getFragmentManager();
                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
                fragmentTransaction3.replace(R.id.fragment_layout, new HookahFragment());
                fragmentTransaction3.commit();
                break;

            default:
                break;
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    private class GetOffers extends AsyncTask {

        String responseData;
        int position = 0;

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                HttpURLConnection connection;
                InputStream is;
                String location = "http://www.oyasisspring.com/kashyapandriod/o2_server_files/offers.json";
                URL url = new URL(location);
                connection = (HttpURLConnection) url.openConnection();
                is = connection.getInputStream();

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
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Getting Special Offers...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
            if (responseData != null) {
                Log.i("Offers", responseData);
                setupViewPager(viewPager, responseData);
            } else {
                Toast.makeText(getContext(), "Could not connect, please try later.", Toast.LENGTH_SHORT).show();
            }
        }

        private void setupViewPager(final ViewPager viewPager, String responseDa) {
            final ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
            try {
                final JSONArray jsonArray = new JSONArray(responseDa);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    adapter.addFragment(OfferFragment.newInstance(String.valueOf(String.valueOf
                            (jsonObject))));
                }
                viewPager.setAdapter(adapter);
                final Handler handler = new Handler();
                final Runnable runnable;
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(position >= jsonArray.length()){
                            position = 0;
                        }else{
                            position = position+1;
                        }
                        viewPager.setCurrentItem(position, true);
                    }
                };
                Timer swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        handler.post(runnable);
                    }
                }, 3500, 3000);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}