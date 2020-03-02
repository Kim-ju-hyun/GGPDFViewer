package com.example.ggpdfviewer;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.io.File;

public class MainActivity extends Activity
    implements GestureDetector.BaseListener {

    private PDFView mPDFView;
    private GestureDetector mGestureDetector;
    private int mPageNumber = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Requests a voice menu on this activity. As for any other window feature,
        // be sure to request this before setContentView() is called
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        // Ensure screen stays on during demo.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main_layout);

        // Initialize the gesture detector and set the activity to listen to discrete gestures.
        mGestureDetector = new GestureDetector(this).setBaseListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPDFView = (PDFView)findViewById(R.id.pdfViewPager);

        String pdfFilePath
                = "/" + Environment.getExternalStorageDirectory().toString()
                + "/" + Environment.DIRECTORY_DCIM
                + "/SFILE/PDF/";

        String pdfFileName = pdfFilePath + "sample.pdf";

        File pdfFile = new File(pdfFileName);

        mPDFView.fromFile(pdfFile)
                .defaultPage(mPageNumber)
                .mask(Color.BLACK, 0)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        mPageNumber = page;
                    }
                })
                .load();

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        final int soundEffect = Sounds.TAP;
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (gesture)
        {
            case TAP:
                assert audioManager != null;
                audioManager.playSoundEffect(soundEffect);
                return true;

            case SWIPE_LEFT:
                assert audioManager != null;
                audioManager.playSoundEffect(soundEffect);
                mPDFView.jumpTo(mPageNumber + 1);
                return true;

            case SWIPE_RIGHT:
                assert audioManager != null;
                audioManager.playSoundEffect(soundEffect);
                mPDFView.jumpTo(mPageNumber - 1);
                return true;

            default:
                return false;
        }
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.voice_menu, menu);
            return true;
        }

        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.voice_menu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.page_back:
                    mPDFView.jumpTo(mPageNumber - 1);
                    break;
                case R.id.page_forward:
                    mPDFView.jumpTo(mPageNumber + 1);
                    break;
                default:
                    return true;
            }
            return true;
        }

        // Good practice to pass through to super if not handled
        return super.onMenuItemSelected(featureId, item);
    }
}
