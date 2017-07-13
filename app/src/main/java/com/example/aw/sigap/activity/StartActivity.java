package com.example.aw.sigap.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.aw.sigap.R;
import com.example.aw.sigap.app.Config;
import com.example.aw.sigap.fragment.DashboardFragment;
import com.example.aw.sigap.fragment.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity implements DashboardFragment.OnFragmentInteractionListener,
    ProfileFragment.OnFragmentInteractionListener{

    Toolbar toolbar;
    TabLayout tablayout;
    ViewPager viewpager;
    private int[] tabIcons = {
            R.drawable.ic_battery_alert_white_48dp,
            R.drawable.ic_battery_alert_white_48dp,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        viewpager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewpager);

        tablayout = (TabLayout) findViewById(R.id.tabs);
        tablayout.setupWithViewPager(viewpager);
        setupTabIcons();
    }
    private void setupTabIcons() {

        tablayout.getTabAt(0).setIcon(R.drawable.ic_battery_alert_white_48dp);
        tablayout.getTabAt(1).setIcon(R.drawable.ic_battery_alert_white_48dp);

    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DashboardFragment(), "DASHBOARD");
        adapter.addFragment(new ProfileFragment(), "PROFILE");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        else if(id == R.id.action_logout){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Exit Application?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            //Getting out sharedpreferences
                            SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            //Getting editor
                            SharedPreferences.Editor editor = preferences.edit();

                            //Puting the value false for loggedin
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                            //Putting blank value to email
                            editor.putString(Config.APIKEY_SHARED_PREF, "");
                            editor.clear();

                            //Saving the sharedpreferences
                            editor.commit();
//                        SharedPreferences user = DashboardActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME,
//                                Context.MODE_PRIVATE);
//                        SharedPreferences apiKey = DashboardActivity.this.getSharedPreferences(Config.SHARED_PREF_API,
//                                Context.MODE_PRIVATE);
//
//                        SharedPreferences.Editor editor = user.edit();
//                        SharedPreferences.Editor editor1 = apiKey.edit();
//
//                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
//                        editor1.putBoolean(Config.API_SHARED_PREF, false);
//
//                        editor.putString(Config.USERNAME_SHARED_PREF, "");
//                        editor1.putString(Config.APIKEY_SHARED_PREF, "");
//
//                        editor.commit();
//                        editor1.commit();

                            //Starting login activity
                            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

            //Showing the alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        else if(id == R.id.action_refresh){
            Intent intent = new Intent(StartActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_profile){
            Intent intent1 = new Intent(StartActivity.this, ProfileActivity.class);
            startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }
}
