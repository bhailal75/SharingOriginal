package com.lsfv.literaturesharing;

import android.app.Dialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.lsfv.literaturesharing.Adapter.DownloadChepterList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadFileActivity extends AppCompatActivity {
    private List<String> myList;
    ListView listView;
    final File directory = Environment.getExternalStorageDirectory();
    File file = new File( directory + "/.Audio Book" );
    final File list[] = file.listFiles();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_file);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.songlistview);
        myList = new ArrayList<String>();

        getData();


    }

    private void getData() {

        myList.clear();


        if (file.length() > 0) {
            for (int i = 0; i < list.length; i++) {
                myList.add(list[i].getName());
            }
        }else {
            Toast.makeText(this, "Downloaded file Not Found", Toast.LENGTH_SHORT).show();
        }
        DownloadChepterList adpt=new DownloadChepterList(DownloadFileActivity.this,R.layout.custom_downloadlist_layout,list);
        listView.setAdapter(adpt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deletebtn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        if (id==R.id.delete){

            // Create custom dialog object
            final Dialog dialog = new Dialog(DownloadFileActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.custom_alert_dialog);

            TextView textView=(TextView)dialog.findViewById(R.id.msg);
            Button buttonNo=(Button)dialog.findViewById(R.id.nobtn);
            Button buttonOk=(Button)dialog.findViewById(R.id.okbtn);

            textView.setText("Are you sure want to delete selected file?");

            buttonNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for( int i=0; i< DownloadChepterList.songPositionList.size(); i++)
                    {
                        list[Integer.parseInt(DownloadChepterList.songPositionList.get(i))].delete();
                    }
                    Toast.makeText(DownloadFileActivity.this, "Selected file Deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());

                }
            });

            dialog.show();

        }
        if (id==R.id.deleteAll){

            // Create custom dialog object
            final Dialog dialog = new Dialog(DownloadFileActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.custom_alert_dialog);

            TextView textView=(TextView)dialog.findViewById(R.id.msg);
            Button buttonNo=(Button)dialog.findViewById(R.id.nobtn);
            Button buttonOk=(Button)dialog.findViewById(R.id.okbtn);

            textView.setText("Are you sure want to delete all downloaded file?");

            buttonNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for( int i=0; i< list.length; i++)
                    {
                        list[i].delete();
                    }
                    Toast.makeText(DownloadFileActivity.this, "All file Deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());

                }
            });

            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

}
