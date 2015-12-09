package com.example.damir.bitflow_android;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    protected PowerManager.WakeLock mWakeLock;

    private static final float VISUALIZER_HEIGHT_DIP = 180f;

    private RelativeLayout mRelativeLayout;

    private MediaPlayer mPlayer;
    private Visualizer mVisualizer;

    private Handler myHandler = new Handler();

    private Button play, pause, stop, backward, forward;
    private TextView currentTrackTime, totalTrackTime, trackTitle;
    private SeekBar trackProgress;

    private VisualizerView mVisualizerView;

    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 5000;
    private int backwardTime = 5000;

    private boolean isPaused = false;


    public static int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Main layout
        mRelativeLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        // Buttons
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);
//        forward = (ImageButton) findViewById(R.id.forward);
//        backward=(ImageButton)findViewById(R.id.backward);

        // Helpers
        currentTrackTime = (TextView) findViewById(R.id.currentTrackTime);
        totalTrackTime = (TextView) findViewById(R.id.totalTrackTime);
        trackTitle = (TextView) findViewById(R.id.trackTitle);
        trackTitle.setText("track.mp3");

        // Player
        mPlayer = MediaPlayer.create(this, R.raw.track);

        setupVisualizerFxAndUI();

        // Make sure the visualizer is enabled only when you actually want to receive data, and
        // when it makes sense to receive data.
        mVisualizer.setEnabled(true);

        // Player helpers
        trackProgress = (SeekBar) findViewById(R.id.trackProgress);
        trackProgress.setClickable(false);
        pause.setEnabled(false);

        trackProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mPlayer.pause();
                isPaused = true;

//                Log.d("qwe", "touch");

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {



                mPlayer.start();
                isPaused = false;

//                Log.d("qwe", "release");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (isPaused) {
                    startTime = progress;
                    mPlayer.seekTo(progress);
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();

                mPlayer.start();

                startTime = mPlayer.getCurrentPosition();
                finalTime = mPlayer.getDuration();

                if (oneTimeOnly == 0) {
                    trackProgress.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }

                long totalMins = TimeUnit.MILLISECONDS.toMinutes((long) finalTime);
                long totalSecs = TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime));

                long currentMins = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
                long currentSecs = TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime));

                totalTrackTime.setText(String.format("%02d:%02d", totalMins, totalSecs));

                currentTrackTime.setText(String.format("%02d:%02d", currentMins, currentSecs));

                trackProgress.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);

                play.setEnabled(false);
                pause.setEnabled(true);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();

                mPlayer.pause();

                play.setEnabled(true);
                pause.setEnabled(false);
            }
        });

        stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                mPlayer.pause();

                mPlayer.seekTo(0);

                play.setEnabled(true);
                pause.setEnabled(false);
            }

        });


//        forward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                int temp = (int)startTime;
//
//                if((temp+forwardTime)<=finalTime){
//
//                    startTime = startTime + forwardTime;
//
//                    mPlayer.seekTo((int) startTime);
//
//                    Toast.makeText(getApplicationContext(),"You have Jumped forward 5 seconds",Toast.LENGTH_SHORT).show();
//
//                }
//                else{
//                    Toast.makeText(getApplicationContext(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        backward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                int temp = (int)startTime;
//
//                if((temp-backwardTime)>0){
//
//                    startTime = startTime - backwardTime;
//
//                    mPlayer.seekTo((int) startTime);
//
//                    Toast.makeText(getApplicationContext(),"You have Jumped backward 5 seconds",Toast.LENGTH_SHORT).show();
//
//                }
//                else{
//                    Toast.makeText(getApplicationContext(),"Cannot jump backward 5 seconds",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private Runnable UpdateSongTime = new Runnable() {

        public void run() {

            if (!isPaused) {

//                Log.d("qwe", "running");

                startTime = mPlayer.getCurrentPosition();

                long mins = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
                long secs = TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime));

                currentTrackTime.setText(String.format("%02d:%02d", mins, secs));

                trackProgress.setProgress((int) startTime);

            } else {

//                Log.d("qwe", "not running");
            }

            myHandler.postDelayed(this, 100);
        }
    };

    private void setupVisualizerFxAndUI() {

        // Create a VisualizerView (defined below), which will render the simplified audio
        // wave form to a Canvas.

        mVisualizerView = new VisualizerView(this);

        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));

        mRelativeLayout.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing() && mPlayer != null) {
            mVisualizer.release();
//            mEqualizer.release();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
