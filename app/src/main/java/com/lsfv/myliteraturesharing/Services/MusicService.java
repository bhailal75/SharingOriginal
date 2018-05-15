package com.lsfv.myliteraturesharing.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.lsfv.myliteraturesharing.OfflinePlayerScreenActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;


public class MusicService extends Service {
    public static WeakReference<TextView> textViewSongTime;
    public static WeakReference<TextView> textViewSongEndTime;
    public static WeakReference<SeekBar> songProgressBar;
    public static WeakReference<Button> btnPlayStop;
    public static WeakReference<Button> btnForword;
    public static WeakReference<Button> btnBackword;
    MediaPlayer mediaPlayer;
    public  boolean playing = false;
    static Handler progressBarHandler = new Handler();
    private  double startTime = 0;
    private  double finalTime = 0;
    public static int oneTimeOnly = 0;
    private int forwardTime = 10000;
    private int backwardTime = 10000;
    String s1,s2;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initUI();
      PlaySong();
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        return START_STICKY;
    }

    private void initUI() {
        textViewSongTime = new WeakReference<>(OfflinePlayerScreenActivity.textViewSongTime);
        textViewSongEndTime = new WeakReference<>(OfflinePlayerScreenActivity.textViewSongEndTime);
        songProgressBar = new WeakReference<>(OfflinePlayerScreenActivity.seekBar);
        btnPlayStop =new WeakReference<Button>(OfflinePlayerScreenActivity.btnPlayStop);
        btnBackword = new WeakReference<Button>(OfflinePlayerScreenActivity.btnBackword);
        btnForword = new WeakReference<Button>(OfflinePlayerScreenActivity.btnForword);

    btnPlayStop.get().setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      //Toast.makeText(MusicService.this, "Hello", Toast.LENGTH_SHORT).show();

        if (playing) {
            //If song is playing and user clicks on Stop button
            //Stop the song by calling stopService() and change boolean value
            //text on button should be changed to 'Play'
            playing = false;
            mediaPlayer.pause();
            btnPlayStop.get().setText("Play");
            progressBarHandler.removeCallbacks(UpdateSongTime);
        } else if (!playing) {
            //If song is not playing and user clicks on Play button
            //Start the song by calling startService() and change boolean value
            //text on button should be changed to 'Stop'
            playing = true;
            btnPlayStop.get().setText("Pause");
            PlaySong();
        }
    }
    });

    btnBackword.get().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int temp = (int) startTime;
            if ((temp - backwardTime) > 0) {
                startTime = startTime - backwardTime;
                mediaPlayer.seekTo((int) startTime);
            } else {
                //The first 5 seconds of the time the music plays before you press the Back key if we are writing to alert you to the condition
                Toast.makeText(getApplicationContext(), "In the first 5 seconds you can't get back to the music",
                        Toast.LENGTH_SHORT).show();
            }
        }
    });

    btnForword.get().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int temp = (int) startTime;
            if ((temp + forwardTime) <= finalTime) {
                startTime = startTime + forwardTime;
                mediaPlayer.seekTo((int) startTime);
            } else {

                //When it came to the last 5 seconds of the time the music plays ,we are writing to alert to the condition if you press the next key

                Toast.makeText(getApplicationContext(), "can't  move the music to forward in the last 5 seconds",
                        Toast.LENGTH_SHORT).show();
            }
        }
    });

    songProgressBar.get().setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(songProgressBar.get().getProgress());
            }

            return false;
        }
    });

    }

    public void onDestroy(){
        //stops the playback
        mediaPlayer.stop();
        playing=false;
        progressBarHandler.removeCallbacks(UpdateSongTime);
        //releases any resource attached with MediaPlayer object
        mediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //The music playing time by updating we provide the sound to continue to play,...
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
             s1= String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)));
            //The music which is in time to show that, we show using a seekbar...span>
            songProgressBar.get().setProgress((int) startTime);
            progressBarHandler.postDelayed(this, 100);
            textViewSongTime.get().setText(s1);
            textViewSongEndTime.get().setText(s2);
        }
    };

    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //INCOMING call
                //do all necessary action to pause the audio
                if(mediaPlayer!=null){//check mp

                    if(mediaPlayer.isPlaying()){

                        mediaPlayer.pause();
                        btnPlayStop.get().setText("Play");
                    }
                }

            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                //Not IN CALL
                //do anything if the phone-state is idle
            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold
                //do all necessary action to pause the audio
                //do something here
                if(mediaPlayer!=null){//check mp

                    if(mediaPlayer.isPlaying()){

                        mediaPlayer.pause();
                        btnPlayStop.get().setText("Play");
                    }
                }
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };//end PhoneStateListener

    void PlaySong(){
        if (mediaPlayer!=null){
            mediaPlayer.start();
            playing=true;
            progressBarHandler.postDelayed(UpdateSongTime, 100);
        }else
        {
            try {
                Log.d("path",OfflinePlayerScreenActivity.absolutePath);
                mediaPlayer = new MediaPlayer();
                //sets the data source of audio file
                mediaPlayer.setDataSource(OfflinePlayerScreenActivity.absolutePath);
                //prepares the player for playback synchronously
                mediaPlayer.prepare();
                //sets the player for looping
                mediaPlayer.setLooping(true);
                //starts or resumes the playback
                mediaPlayer.start();

                playing=true;

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

           /* if (oneTimeOnly == 0) {

                oneTimeOnly = 1;
            }*/
                songProgressBar.get().setMax((int) finalTime);
                //Of music is how much they have in total,we print in  endTimeField controller
                s2= String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) finalTime)));
                //The elapsed time from the moment ,send to startTimeField controller
                s1= String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)));

                textViewSongTime.get().setText(s1);
                textViewSongEndTime.get().setText(s2);


                progressBarHandler.postDelayed(UpdateSongTime, 100);



            } catch (IOException e) {
                e.printStackTrace();
                Log.i("show","Error: "+e.toString());
            }
        }


    }
}
