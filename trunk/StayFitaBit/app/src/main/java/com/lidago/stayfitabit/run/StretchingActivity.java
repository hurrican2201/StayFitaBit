package com.lidago.stayfitabit.run;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.lidago.stayfitabit.Args;
import com.lidago.stayfitabit.R;

import org.w3c.dom.Text;

public class StretchingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageSwitcher mImageSwitcher;
    private TextView mDescriptionTextView;
    private TextView mNumberTextView;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private int mState = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretching);
        init();
    }

    private void init() {
        setupUserInterface();
        setupToolbar();
        setListener();
        loadAnimations();
        setFactory();
    }

    private void setupUserInterface() {
        mToolbar = (Toolbar) findViewById(R.id.stretching_toolbar);
        mImageSwitcher = (ImageSwitcher) findViewById(R.id.stretching_imageSwitcher);
        mDescriptionTextView = (TextView) findViewById(R.id.stretching_description_textView);
        mDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        mNumberTextView = (TextView) findViewById(R.id.stretching_number_textView);
        mPreviousButton = (ImageButton) findViewById(R.id.stretching_previous_imageButton);
        mNextButton = (ImageButton) findViewById(R.id.stretching_next_imageButton);
        mDescriptionTextView.setText(getString(R.string.stretching_practice_1));
        mNumberTextView.setText(String.format("%d/%d", mState, 3));
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        // We have a Toolbar in place so we don't need to care about the NPE warning
        getSupportActionBar().setTitle(getString(R.string.stretching));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setListener() {
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPreviousImage();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNextImage();
            }
        });
    }

    private void loadAnimations() {
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        mImageSwitcher.setInAnimation(in);
        mImageSwitcher.setOutAnimation(out);
    }

    private void setFactory() {
        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView image = new ImageView(getApplicationContext());
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                FrameLayout.LayoutParams params = new ImageSwitcher.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

                image.setLayoutParams(params);
                return image;
            }
        });
        mImageSwitcher.setImageResource(R.drawable.stretching_1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayPreviousImage() {
        if(mState == 3) {
            mState--;
            mImageSwitcher.setImageResource(R.drawable.stretching_2);
            mNumberTextView.setText(String.format("%d/%d", mState, 3));
            mDescriptionTextView.setText(getString(R.string.stretching_practice_2));
            mNextButton.setEnabled(true);
        }
        else if(mState == 2) {
            mState--;
            mImageSwitcher.setImageResource(R.drawable.stretching_1);
            mNumberTextView.setText(String.format("%d/%d", mState, 3));
            mDescriptionTextView.setText(getString(R.string.stretching_practice_1));
            mPreviousButton.setEnabled(false);
        }
    }

    private void displayNextImage() {
        if(mState == 1) {
            mState++;
            mImageSwitcher.setImageResource(R.drawable.stretching_2);
            mNumberTextView.setText(String.format("%d/%d", mState, 3));
            mDescriptionTextView.setText(getString(R.string.stretching_practice_2));
            mPreviousButton.setEnabled(true);
        }
        else if(mState == 2) {
            mState++;
            mImageSwitcher.setImageResource(R.drawable.stretching_3);
            mNumberTextView.setText(String.format("%d/%d", mState, 3));
            mDescriptionTextView.setText(getString(R.string.stretching_practice_3));
            mNextButton.setEnabled(false);
        }
    }
}
