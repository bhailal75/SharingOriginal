package com.lsfv.myliteraturesharing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.lsfv.myliteraturesharing.Adapter.AdapterClass;
import com.lsfv.myliteraturesharing.model.SongObject;
import com.lsfv.myliteraturesharing.Services.MusicService;

import java.io.File;
import java.util.ArrayList;

public class OfflinePlayerScreenActivity extends AppCompatActivity {
    ListView listview;
    public static TextView textViewSongTime,textViewSongEndTime;
    public static SeekBar seekBar;
    Button btnNext,btnPrevious;
    public static Button btnPlayStop, btnForword, btnBackword;
    TextView txtSongName;
    CardView cardView;
    ArrayList<SongObject> listOfContents;
    AdapterClass adapter;
    String path;
    public static String absolutePath, songName;
    public static boolean playing = false;
    int position;
    int listsize;
    void initViews() {
        //initializing views
        btnPlayStop = (Button) findViewById(R.id.btnPlayStop);
        btnBackword =(Button)findViewById(R.id.btnBackword);
        btnForword =(Button)findViewById(R.id.btnForward);
        btnNext=(Button)findViewById(R.id.btnNext);
        btnPrevious=(Button)findViewById(R.id.btnPrevious);
        txtSongName = (TextView) findViewById(R.id.txtSongName);
        cardView = (CardView) findViewById(R.id.cardView);
        listview = (ListView) findViewById(R.id.listView);
        listOfContents = new ArrayList<>();
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textViewSongTime = (TextView) findViewById(R.id.textViewSongTime);
        textViewSongEndTime=(TextView) findViewById(R.id.textViewSongendTime);

        //If music is playing already on opening starting the app, player should be visible with Stop button
        if (playing) {
            txtSongName.setText(songName);
            cardView.setVisibility(View.VISIBLE);
            btnPlayStop.setText("Pause");
        }

        //Gives you the full path of phone memory
        path = Environment.getExternalStorageDirectory().getAbsolutePath();

        //Calling the function which fetches the list of music files
        initList(path);

        listsize=listOfContents.size();

       /* //initializing the adapter and passing the context, list item and list of references of SongObject
        adapter = new AdapterClass(this, R.layout.list_item, listOfContents);
        listview.setAdapter(adapter);
*/
        play(position);

      /*  //handling events when user clicks on any music file in list view
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                play(position);

            }

        });*/

       /* //Handling events when button Play/Stop is clicked in the player
        btnPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    //If song is playing and user clicks on Stop button
                    //Stop the song by calling stopService() and change boolean value
                    //text on button should be changed to 'Play'
                    playing = false;
                    btnPlayStop.setText("Play");
                    //btnPlayStop.setVisibility(View.GONE);
                    Intent i = new Intent(OfflinePlayerScreenActivity.this, MusicService.class);
                    stopService(i);
                } else if (!playing) {
                    //If song is not playing and user clicks on Play button
                    //Start the song by calling startService() and change boolean value
                    //text on button should be changed to 'Stop'
                    playing = true;
                    btnPlayStop.setText("Stop");
                    Intent i = new Intent(OfflinePlayerScreenActivity.this, MusicService.class);
                    startService(i);
                }
            }
        });*/

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < (listsize-1)){
                    position=position+1;
                   // Toast.makeText(OfflinePlayerScreenActivity.this, "Next "+position, Toast.LENGTH_SHORT).show();
                    play(position);
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0){
                    position=position-1;
                   // Toast.makeText(OfflinePlayerScreenActivity.this, "Previous "+position, Toast.LENGTH_SHORT).show();
                    play(position);
                }
            }
        });
    }

    //Fetching .mp3 and .mp4 files from phone storage
    void initList(String path) {
        try {
            File file = new File(path + "/.Audio Book");
            File[] filesArray = file.listFiles();
            String fileName;
            for (File file1 : filesArray) {
                if (file1.isDirectory()) {
                    initList(file1.getAbsolutePath());
                } else {
                    fileName = file1.getName();
                    listOfContents.add(new SongObject(file1.getName(), file1.getAbsolutePath()));


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void play(int position){
        //player is visible
        cardView.setVisibility(View.VISIBLE);

        //If some other song is already playing, stop the service
        if (playing) {
            Intent i = new Intent(OfflinePlayerScreenActivity.this, MusicService.class);
            stopService(i);
        }

        playing = true;

        //getting absolute path of selected song from bean class 'SongObject'
        SongObject sdOb = listOfContents.get(position);
        absolutePath = sdOb.getAbsolutePath();

        //Play the selected song by starting the service
        Intent start = new Intent(OfflinePlayerScreenActivity.this, MusicService.class);
        startService(start);

        //Get and set the name of song in the player
        songName = listOfContents.get(position).getFileName();
      //  Toast.makeText(this, ""+songName, Toast.LENGTH_SHORT).show();
        txtSongName.setText(songName);
        btnPlayStop.setText("Pause");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_player_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        position=getIntent().getIntExtra("position",0);
        initViews();
    }

    @Override
    public void onBackPressed() {
        if (playing) {
            Intent i = new Intent(OfflinePlayerScreenActivity.this, MusicService.class);
            stopService(i);
            playing=false;
        }
        super.onBackPressed();


    }
    /*    @Override
    protected void onDestroy() {
        Intent start = new Intent(OfflinePlayerScreenActivity.this, MusicService.class);
        stopService(start);
        super.onDestroy();
    }*/

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
