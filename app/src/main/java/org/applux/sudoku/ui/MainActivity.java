package org.applux.sudoku.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.InterstitialAd;
import com.huawei.hms.ads.banner.BannerView;


import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.applux.sudoku.controller.GameStateManager;
import org.applux.sudoku.controller.NewLevelManager;
import org.applux.sudoku.controller.helper.GameInfoContainer;
import org.applux.sudoku.game.GameDifficulty;
import org.applux.sudoku.game.GameType;
import org.applux.sudoku.ui.view.R;

import java.util.LinkedList;
import java.util.List;

import static org.applux.sudoku.ui.TutorialActivity.ACTION_SHOW_ANYWAYS;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private InterstitialAd interstitialAd;
    private BannerView bannerView;


    SharedPreferences settings;

    DrawerLayout drawer;
    NavigationView mNavigationView;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        NewLevelManager newLevelManager = NewLevelManager.getInstance(getApplicationContext(), settings);

        // check if we need to pre generate levels.
        newLevelManager.checkAndRestock();

        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

            /*
          The {@link android.support.v4.view.PagerAdapter} that will provide
          fragments for each of the sections. We use a
          {@link FragmentPagerAdapter} derivative, which will keep every
          loaded fragment in memory. If this becomes too memory intensive, it
          may be best to switch to a
          {@link android.support.v4.app.FragmentStatePagerAdapter}.
         */



        // set Nav_Bar
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view_main);
        mNavigationView.setNavigationItemSelectedListener(this);

        selectNavigationItem(R.id.nav_newgame_main);

        overridePendingTransition(0, 0);

        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);

        bannerView = findViewById(R.id.hw_banner_view);

        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);
    }

    public void callFragment(View view){
        /*FragmentManager fm = getSupportFragmentManager();
        DialogWinScreen winScreen = new DialogWinScreen();

        winScreen.show(fm,"win_screen_layout");*/

    }


    public void onClick(@org.jetbrains.annotations.NotNull View view) {
        this.view = view;

        Intent i = null;

        switch(view.getId()) {
            case R.id.continueButton:
                i = new Intent(this, LoadGameActivity.class);
                break;
            case R.id.new_game:
                i = new Intent(this, choiceActivity.class);
                break;
        }

        final Intent intent = i;

        if(intent != null) {

            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, MAIN_CONTENT_FADEOUT_DURATION);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        selectNavigationItem(R.id.nav_newgame_main);

        refreshContinueButton();
    }

    private void refreshContinueButton() {
        // enable continue button if we have saved games.
        Button continueButton = (Button)findViewById(R.id.continueButton);
        GameStateManager fm = new GameStateManager(getBaseContext(), settings);
        List<GameInfoContainer> gic = fm.loadGameStateInfo();
        if(gic.size() > 0) {
            continueButton.setEnabled(true);
            continueButton.setBackgroundResource(R.drawable.standalone_button);
        } else {
            continueButton.setEnabled(false);
            continueButton.setBackgroundResource(R.drawable.inactive_button);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);

        // return if we are not going to another page
        if(id == R.id.nav_newgame_main) {
            return true;
        }

        // delay transition so the drawer can close
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNavigationItem(id);
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        // fade out the active activity
        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }

        return true;
    }

    // set active navigation item
    private void selectNavigationItem(int itemId) {
        for(int i = 0 ; i < mNavigationView.getMenu().size(); i++) {
            boolean b = itemId == mNavigationView.getMenu().getItem(i).getItemId();
            mNavigationView.getMenu().getItem(i).setChecked(b);
        }
    }

    private boolean goToNavigationItem(int id) {
        Intent intent;

        switch(id) {
            case R.id.menu_settings_main:
                //open settings
                intent = new Intent(this,SettingsActivity.class);
                intent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GamePreferenceFragment.class.getName() );
                intent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_highscore_main:
                // see highscore list

                intent = new Intent(this, StatsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

                loadInterstitialAd();
                break;

//            case R.id.menu_about_main:
//                //open about page
//                intent = new Intent(this,AboutActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//                break;

            case R.id.menu_help_main:
                //open about page
                intent = new Intent(this,HelpActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.menu_tutorial_main:
                intent = new Intent(this, TutorialActivity.class);
                intent.setAction(ACTION_SHOW_ANYWAYS);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            default:
        }
        return true;
    }



    private void loadInterstitialAd() {

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdId(getString(R.string.ad_id_interstitial)); // "testb4znbuh3n2" is a dedicated test ad slot ID.
        interstitialAd.setAdListener(adListener);

        AdParam adParam = new AdParam.Builder().build();
        interstitialAd.loadAd(adParam);
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            // Display an interstitial ad.
            showInterstitial();
        }
    };

    private void showInterstitial() {
        // Display the ad.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }

}
