package com.lsfv.myliteraturesharing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lsfv.myliteraturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.myliteraturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.myliteraturesharing.Helper.Config;
import com.lsfv.myliteraturesharing.Helper.Utils;
import com.lsfv.myliteraturesharing.model.ChapterListModel;
import com.lsfv.myliteraturesharing.model.OnlineSongModel;
import com.lsfv.myliteraturesharing.Services.MusicServiceOnline;

import java.util.ArrayList;

public class PlayerScreenActivity extends AppCompatActivity {
    ArrayList<OnlineSongModel> chapterList;
    int position;
    public static TextView textViewSongTime,textViewSongEndTime;
    public static SeekBar seekBar;
    public static Button btnPlayStop, btnForword, btnBackword;
    Button btnNext,btnPrevious;
    TextView txtSongName;
    CardView cardView;
    ChapterListModel model;
    public static String absolutePath, songName;
    public static String finaltm;
    public static boolean playing = false;
    String s,type;
    int listsize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnPlayStop = (Button) findViewById(R.id.btnPlayStop);
        btnBackword =(Button)findViewById(R.id.btnBackword);
        btnForword =(Button)findViewById(R.id.btnForward);
        btnNext=(Button)findViewById(R.id.btnNext);
        btnPrevious=(Button)findViewById(R.id.btnPrevious);
        txtSongName = (TextView) findViewById(R.id.txtSongName);
        cardView = (CardView) findViewById(R.id.cardView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textViewSongTime = (TextView) findViewById(R.id.textViewSongTime);
        textViewSongEndTime=(TextView) findViewById(R.id.textViewSongendTime);


        position = getIntent().getIntExtra("position", 0);
        absolutePath = getIntent().getStringExtra("link");
        txtSongName.setText(getIntent().getStringExtra("name"));
        finaltm=getIntent().getStringExtra("duration");
        type=getIntent().getStringExtra("type");
       // Toast.makeText(this, ""+type, Toast.LENGTH_SHORT).show();


        SharedPreferences preferences = getApplicationContext().getSharedPreferences("bookid", Context.MODE_PRIVATE);
        s = preferences.getString("id", null);

        String[]keys=new String[]{"mode","audio_book_id","file_recent"};
        String[]values=new String[]{"getchapter",s,type};
        String jsonRequest= Utils.createJsonRequest(keys,values);

        String URL = Config.MAIN_URL;
        new WebserviceCall(PlayerScreenActivity.this, URL, jsonRequest, "Song Playing...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("myapp",response);
                model = new Gson().fromJson(response,ChapterListModel.class);
//                Toast.makeText(getContext(),model.getMessage() , Toast.LENGTH_SHORT).show();
                if (model.getStatus().equalsIgnoreCase("1"))
                {
                    chapterList=new ArrayList<>();
                    listsize=model.getChapterList().size();
                    for (int i=0;i<listsize;i++)
                    {
                        chapterList.add(new OnlineSongModel(model.getChapterList().get(i).getChapter_desc(),model.getChapterList().get(i).getChapter_file(),model.getChapterList().get(i).getDuration()));
                        //Toast.makeText(PlayerScreenActivity.this, ""+chapterList.get(i).getChapter_file(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(PlayerScreenActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        }).execute();

        play();


        /*btnPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    //If song is playing and user clicks on Stop button
                    //Stop the song by calling stopService() and change boolean value
                    //text on button should be changed to 'Play'
                    playing = false;
                    btnPlayStop.setText("Play");
                    //btnPlayStop.setVisibility(View.GONE);
                    Intent i = new Intent(PlayerScreenActivity.this, MusicServiceOnline.class);
                    stopService(i);
                } else if (!playing) {
                    //If song is not playing and user clicks on Play button
                    //Start the song by calling startService() and change boolean value
                    //text on button should be changed to 'Stop'
                    playing = true;
                    btnPlayStop.setText("Stop");
                    Intent i = new Intent(PlayerScreenActivity.this, MusicServiceOnline.class);
                    startService(i);
                }
            }
        });*/

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < (listsize-1)){
                    position=position+1;
                    absolutePath=chapterList.get(position).getChapter_file();
                    finaltm=chapterList.get(position).getDuration();
                    txtSongName.setText(chapterList.get(position).getChapter_desc());
                   // Toast.makeText(PlayerScreenActivity.this, "Next "+position, Toast.LENGTH_SHORT).show();
                   // Toast.makeText(PlayerScreenActivity.this, ""+chapterList.get(position).getChapter_desc()+finaltm+absolutePath, Toast.LENGTH_SHORT).show();
                    play();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0){
                    position=position-1;
                   // Toast.makeText(PlayerScreenActivity.this, "Previous "+position, Toast.LENGTH_SHORT).show();
                    absolutePath=chapterList.get(position).getChapter_file();
                    finaltm=chapterList.get(position).getDuration();
                    txtSongName.setText(chapterList.get(position).getChapter_desc());
                    play();
                }
            }
        });
    }



    void play(){
        //player is visible
        cardView.setVisibility(View.VISIBLE);

        //If some other song is already playing, stop the service
        if (playing) {
            Intent i = new Intent(PlayerScreenActivity.this, MusicServiceOnline.class);
            stopService(i);
        }

        playing = true;


       // Toast.makeText(this, ""+absolutePath+songName+finaltm, Toast.LENGTH_SHORT).show();

        //getting absolute path of selected song from bean class 'SongObject'
      /*  absolutePath = model.getChapterList().get(position).getChapter_file();
        Toast.makeText(this, ""+absolutePath, Toast.LENGTH_SHORT).show();
        Log.d("path",absolutePath);*/

    /*    OnlineSongModel sdOb = chapterList.get(position);
        absolutePath = sdOb.getChapter_file();*/


        //Play the selected song by starting the service
        Intent start = new Intent(PlayerScreenActivity.this, MusicServiceOnline.class);
        startService(start);

       /* //Get and set the name of song in the player
        songName = chapterList.get(position).getChapter_file();

        txtSongName.setText(songName);*/
        btnPlayStop.setText("Pause");


    }

    @Override
    public void onBackPressed() {
        if (playing) {
            Intent i = new Intent(PlayerScreenActivity.this, MusicServiceOnline.class);
            stopService(i);
            playing = false;
        }
        super.onBackPressed();


    }
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
