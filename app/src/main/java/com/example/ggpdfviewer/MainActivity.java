package com.example.ggpdfviewer;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;

import java.io.File;

public class MainActivity extends Activity
    implements GestureDetector.BaseListener {

    private PDFView mPDFView;
    private GestureDetector mGestureDetector;
    private int mPageNumber = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

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

}
