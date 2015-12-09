package com.example.damir.bitflow_android;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private Button
            play,
            pause,
            backward,
            forward;

    private MediaPlayer mPlayer;
    private Handler myHandler = new Handler();
    private SeekBar trackProgress;
    private TextView
            currentTrackTime,
            totalTrackTime,
            trackTitle;

    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 5000;
    private int backwardTime = 5000;


    public static int oneTimeOnly = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play=(Button)findViewById(R.id.play);
        forward = (Button) findViewById(R.id.forward);
        pause = (Button) findViewById(R.id.pause);
        backward=(Button)findViewById(R.id.backward);

        currentTrackTime=(TextView)findViewById(R.id.currentTrackTime);
        totalTrackTime=(TextView)findViewById(R.id.totalTrackTime);
        trackTitle=(TextView)findViewById(R.id.trackTitle);
        trackTitle.setText("test.mp3");

        mPlayer = MediaPlayer.create(this, R.raw.test);
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

            currentTrackTime.setText(String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );

            trackProgress.setProgress((int)startTime);

            myHandler.postDelayed(this, 100);
        }
    };
}
