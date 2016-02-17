package com.ospring.o2lounge.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ospring.o2lounge.R;
import com.ospring.o2lounge.fragments.AboutFragment;
import com.ospring.o2lounge.fragments.FoodFragment;
import com.ospring.o2lounge.fragments.HomeFragment;
import com.ospring.o2lounge.fragments.HookahFragment;
import com.ospring.o2lounge.fragments.LocateFragment;
import com.ospring.o2lounge.fragments.OrderHistoryFragment;
import com.ospring.o2lounge.others.Constants;
import com.ospring.o2lounge.others.DBAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    Toolbar toolbar;
    TextView tv_cart_item = null;
    Menu _menu = null;
    DBAdapter myDb;
    NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_NAME,
                MODE_PRIVATE);
        boolean b = sharedPreferences.getBoolean(Constants.KEY_SIGNED_OR_NOT, false);
        boolean b1 = sharedPreferences.getBoolean(Constants.KEY_SKIP_FLAG, false);
        String s = sharedPreferences.getString(Constants.SIGNED_CLIENT, "None");
        if (b || b1) {
            setContentView(R.layout.activity_main);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(0xFFFFFFFF);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            startHomeFragment();
            switch (s){
                case "F":

            }
        } else {
            startActivity(new Intent(this, SignUpLog.class));
            finish();
        }
    }

    private void startHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_layout, new HomeFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
            startActivity(new Intent(Intent.CATEGORY_HOME));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_NAME,
                MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Constants.KEY_SIGNED_OR_NOT, false)) {
            getMenuInflater().inflate(R.menu.main, menu);
            String count = getCartCount();
            setCartCount(count, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_signed_in, menu);
            String count = getCartCount();
            setCartCount(count, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void setCartCount(String count, Menu menu) {
        this._menu = menu;
        MenuItem item = menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(item, R.layout.action_bar_notification_icon);
        View notifCount = MenuItemCompat.getActionView(item);
        tv_cart_item = (TextView) notifCount.findViewById(R.id.tv_cart_item_count);
        tv_cart_item.setText(count);
    }

    private String getCartCount() {
        myDb = new DBAdapter(this);
        myDb.open();
        String s = myDb.getCartCount();
        myDb.close();
        return s;
    }

    public void displayCart(View view) {
        startActivity(new Intent(this, CartActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_signed_in:
                SharedPreferences sharedPreferences = getSharedPreferences(Constants
                        .SHARED_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.KEY_SIGN_MAIL, null);
                editor.putBoolean(Constants.KEY_SIGNED_OR_NOT, false);
                switch (sharedPreferences.getString(Constants.SIGNED_CLIENT, "None")) {
                    case "F":
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        LoginManager.getInstance().logOut();
                        Toast.makeText(MainActivity.this, "You are signed out from Facebook", Toast
                                .LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
                editor.putString(Constants.SIGNED_CLIENT, null);
                editor.apply();
                Toast.makeText(MainActivity.this, "Signed out!", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                } else {
                    finish();
                }
                break;

            case R.id.action_signed_out:
                startActivity(new Intent(this, SignUpLog.class));
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                toolbar.setTitle("Home");
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStackImmediate(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_layout, new HomeFragment());
                ft.commit();
                break;

            case R.id.nav_food:
                toolbar.setTitle("Food");
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStackImmediate(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new FoodFragment());
                fragmentTransaction.commit();
                break;

            case R.id.nav_hookah:
                toolbar.setTitle("Hookah");
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.popBackStackImmediate(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                fragmentTransaction1.replace(R.id.fragment_layout, new HookahFragment());
                fragmentTransaction1.commit();
                break;

            case R.id.nav_locate_us:
                toolbar.setTitle("Locate Us");
                Intent intent = new Intent(MainActivity.this, LocateFragment.class);
                startActivity(intent);
                break;

            case R.id.nav_about_us:
                toolbar.setTitle("About Us");
                FragmentManager fragmentManager3 = getSupportFragmentManager();
                fragmentManager3.popBackStackImmediate(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
                fragmentTransaction3.replace(R.id.fragment_layout, new AboutFragment());
                fragmentTransaction3.commit();
                break;

            case R.id.nav_share:
                toolbar.setTitle("Share");
                Intent sendIntent1 = new Intent();
                sendIntent1.setAction(Intent.ACTION_SEND);
                sendIntent1.putExtra(Intent.EXTRA_TEXT,
                        "Check out this amazing app of O2 Coffee Lounge at http://www.oyasisspring.com/sowjanya/o2/");
                sendIntent1.setType("text/plain");
                startActivity(sendIntent1);
                break;

            case R.id.nav_feedback:
                toolbar.setTitle("Feedback");
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@o2coffeelounge.com"});
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for O2-Lounge App");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is Feedback for O2-Lounge (Enter " +
                        "after this)");
                startActivity(sendIntent);
                break;

            case R.id.nav_order_history:
                SharedPreferences sharedPreferences = getSharedPreferences(Constants
                        .SHARED_PREFS_NAME, MODE_PRIVATE);
                String s = sharedPreferences.getString(Constants.KEY_SIGN_MAIL, null);
                if (s == null) {
                    Toast.makeText(MainActivity.this, "Log in to view your order history", Toast.LENGTH_SHORT).show();
                } else {
                    toolbar.setTitle("Order history");
                    FragmentManager fragmentManager4 = getSupportFragmentManager();
                    fragmentManager4.popBackStackImmediate(null, android.app.FragmentManager
                            .POP_BACK_STACK_INCLUSIVE);
                    FragmentTransaction fragmentTransaction4 = fragmentManager4.beginTransaction();
                    fragmentTransaction4.replace(R.id.fragment_layout, new OrderHistoryFragment());
                    fragmentTransaction4.commit();
                }
                break;

            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        toolbar.setTitle("Home");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Connection failed.", Toast.LENGTH_SHORT).show();
    }
}