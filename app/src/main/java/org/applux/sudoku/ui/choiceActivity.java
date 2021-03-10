package org.applux.sudoku.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.applux.sudoku.controller.NewLevelManager;
import org.applux.sudoku.game.GameDifficulty;
import org.applux.sudoku.game.GameType;
import org.applux.sudoku.ui.view.R;

import java.util.List;

import static org.applux.sudoku.ui.BaseActivity.MAIN_CONTENT_FADEOUT_DURATION;

public class choiceActivity extends AppCompatActivity {
    SharedPreferences settings;
    private ViewPager viewPager;
    ImageView arrowLeft, arrowRight;
    Handler mHandler;
    private PrefManager prefManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        NewLevelManager newLevelManager = NewLevelManager.getInstance(getApplicationContext(), settings);

        // check if we need to pre generate levels.
        newLevelManager.checkAndRestock();

        setContentView(R.layout.activity_main_slide1);

        final choiceActivity.SectionsPagerAdapter mSectionsPagerAdapter = new choiceActivity.SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.scroller);
        viewPager.setAdapter(mSectionsPagerAdapter);

        // set default gametype choice to whatever was chosen the last time.
        List<GameType> validGameTypes = GameType.getValidGameTypes();
        String lastChosenGameType = settings.getString("lastChosenGameType", GameType.Default_9x9.name());
        int index = validGameTypes.indexOf(Enum.valueOf(GameType.class, lastChosenGameType));

        viewPager.setCurrentItem(index);
        arrowLeft = (ImageView)findViewById(R.id.arrow_left);
        arrowRight = (ImageView) findViewById(R.id.arrow_right);

        //care for initial postiton of the ViewPager
        arrowLeft.setVisibility((index==0)? View.INVISIBLE:View.VISIBLE);
        arrowRight.setVisibility((index==mSectionsPagerAdapter.getCount()-1)?View.INVISIBLE:View.VISIBLE);

        //Update ViewPager on change
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                arrowLeft.setVisibility((position==0)?View.INVISIBLE:View.VISIBLE);
                arrowRight.setVisibility((position==mSectionsPagerAdapter.getCount()-1)?View.INVISIBLE:View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    public void onClick(View view) {

        Intent i = null;

        switch(view.getId()) {
            case R.id.arrow_left:
                viewPager.arrowScroll(View.FOCUS_LEFT);
                break;
            case R.id.arrow_right:
                viewPager.arrowScroll(View.FOCUS_RIGHT);
                break;
            case R.id.next:
                i = new Intent(this, MainSliderActivity.class);
                break;

            default:
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a GameTypeFragment (defined as a static inner class below).
            return choiceActivity.GameTypeFragment.newInstance(position);
        }



        @Override
        public int getCount() {
            // Show 3 total pages.
            return GameType.getValidGameTypes().size();
        }
    }
    public static class GameTypeFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */


        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static GameTypeFragment newInstance(int sectionNumber) {
            GameTypeFragment fragment = new GameTypeFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public GameTypeFragment() {

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

            GameType gameType = GameType.getValidGameTypes().get(getArguments().getInt(ARG_SECTION_NUMBER));

            ImageView imageView = (ImageView) rootView.findViewById(R.id.gameTypeImage);

            imageView.setImageResource(gameType.getResIDImage());


            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(gameType.getStringResID()));
            return rootView;
        }
    }


}
