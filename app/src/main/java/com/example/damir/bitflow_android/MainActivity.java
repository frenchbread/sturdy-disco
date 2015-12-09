package com.example.damir.bitflow_android;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private static final float VISUALIZER_HEIGHT_DIP = 200f;

    private RelativeLayout mRelativeLayout;

    private MediaPlayer mPlayer;
    private Visualizer mVisualizer;

    private Handler myHandler = new Handler();

    private Button play, pause, backward, forward;
    private TextView currentTrackTime, totalTrackTime, trackTitle;
    private SeekBar trackProgress;

    private VisualizerView mVisualizerView;

    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 5000;
    private int backwardTime = 5000;


    public static int oneTimeOnly = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Main layout
        mRelativeLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        // Buttons
        play=(Button)findViewById(R.id.play);
        forward = (Button) findViewById(R.id.forward);
        pause = (Button) findViewById(R.id.pause);
        backward=(Button)findViewById(R.id.backward);

        // Helpers
        currentTrackTime=(TextView)findViewById(R.id.currentTrackTime);
        totalTrackTime=(TextView)findViewById(R.id.totalTrackTime);
        trackTitle=(TextView)findViewById(R.id.trackTitle);
        trackTitle.setText("test.mp3");

        // Player
        mPlayer = MediaPlayer.create(this, R.raw.track);

        setupVisualizerFxAndUI();

        // Make sure the visualizer is enabled only when you actually want to receive data, and
        // when it makes sense to receive data.
        mVisualizer.setEnabled(true);

        // Player helpers
        trackProgress=(SeekBar)findViewById(R.id.trackProgress);
        trackProgress.setClickable(false);
        pause.setEnabled(false);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();

                mPlayer.start();

                startTime = mPlayer.getCurrentPosition();
                finalTime = mPlayer.getDuration();

                if (oneTimeOnly == 0) {
                    trackProgress.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }
                totalTrackTime.setText(String.format(
                        "%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );

                currentTrackTime.setText(String.format(
                        "%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );

                trackProgress.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime,100);
                pause.setEnabled(true);
                play.setEnabled(false);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();

                mPlayer.pause();
                pause.setEnabled(false);
                play.setEnabled(true);
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int temp = (int)startTime;

                if((temp+forwardTime)<=finalTime){

                    startTime = startTime + forwardTime;

                    mPlayer.seekTo((int) startTime);

                    Toast.makeText(getApplicationContext(),"You have Jumped forward 5 seconds",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int temp = (int)startTime;

                if((temp-backwardTime)>0){

                    startTime = startTime - backwardTime;

                    mPlayer.seekTo((int) startTime);

                    Toast.makeText(getApplicationContext(),"You have Jumped backward 5 seconds",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Cannot jump backward 5 seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Runnable UpdateSongTime = new Runnable() {

        public void run() {

            startTime = mPlayer.getCurrentPosition();

            long mins = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
            long secs = TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime));

            currentTrackTime.setText(String.format("%02d:%02d", mins, secs));

            trackProgress.setProgress((int)startTime);

            myHandler.postDelayed(this, 100);
        }
    };

    private void setupVisualizerFxAndUI() {

        // Create a VisualizerView (defined below), which will render the simplified audio
        // wave form to a Canvas.

        mVisualizerView = new VisualizerView(this);

        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                (int)(VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));

        mRelativeLayout.addView(mVisualizerView);



        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}
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
