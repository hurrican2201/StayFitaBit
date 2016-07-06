package com.lidago.stayfitabit;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lidago.stayfitabit.firebase.FirebaseClient;
import com.lidago.stayfitabit.history.HistoryFragment;
import com.lidago.stayfitabit.home.HomeFragment;
import com.lidago.stayfitabit.login.LoginActivity;
import com.lidago.stayfitabit.pushups.PushUpsFragment;
import com.lidago.stayfitabit.run.RunFragment;
import com.lidago.stayfitabit.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private static DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void init() {
        setupUserInterface();
        setupToolbar();
        setupDrawerToggle();
        setupNavigation();
        if(FirebaseClient.getInstance().isUserInitialize())
            switchFragment(R.id.menu_navigation_home);
        else
            switchFragment(R.id.menu_navigation_settings);
    }

    private void setupUserInterface() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
    }

    private void setupDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle
                (this, mDrawerLayout, mToolbar, R.string.open_navigation, R.string.close_navigation);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void setupNavigation() {
        if(FirebaseClient.getInstance().isUserInitialize())
            mNavigationView.getMenu().getItem(0).setChecked(true);
        else
            mNavigationView.getMenu().getItem(4).setChecked(true);

        mNavigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        item.setChecked(true);
                        switchFragment(item.getItemId());
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

    }

    private void switchFragment(int itemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment newFragment = null;
        switch (itemId) {
            case R.id.menu_navigation_home:
                newFragment = HomeFragment.newInstance(getString(R.string.app_name));
                break;
            case R.id.menu_navigation_run:
                newFragment = RunFragment.newInstance(getString(R.string.run));
                break;
            case R.id.menu_navigation_pushups:
                newFragment = PushUpsFragment.newInstance(getString(R.string.pushups));
                break;
            case R.id.menu_navigation_history:
                newFragment = HistoryFragment.newInstance(getString(R.string.history));
                break;
            case R.id.menu_navigation_settings:
                newFragment = SettingsFragment.newInstance(getString(R.string.settings));
                break;
            case R.id.menu_navigation_logout:
                logout();
                break;
            default:
                newFragment = null;
        }
        if (newFragment != null) {
            fragmentTransaction.replace(R.id.main_container, newFragment).commit();
        }
    }

    private void logout() {
        FirebaseClient.getInstance().logout();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public static void enableNavigationDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public static void disableNavigationDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}
