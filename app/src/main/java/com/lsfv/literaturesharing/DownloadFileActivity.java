package com.lsfv.literaturesharing;

import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.lsfv.literaturesharing.Adapter.DownloadBookListAdapter;
import com.lsfv.literaturesharing.Adapter.DownloadChapterListAdapter;
import com.lsfv.literaturesharing.Fragment.OfflineChapterFragment;
import com.lsfv.literaturesharing.model.AudiobookBean;
import com.lsfv.literaturesharing.model.AudiobookBeanEntityManager;
import com.lsfv.literaturesharing.model.ChapterListBean;
import com.lsfv.literaturesharing.model.ChapterListBeanEntityManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadFileActivity extends AppCompatActivity implements DownloadBookListAdapter.OfflineBookClickListner {
    private List<String> myList;
//    ListView listView;
    final File directory = Environment.getExternalStorageDirectory();
    File file = new File(directory + "/" + "Literature sharing for VI" + "/");
    final File list[] = file.listFiles();
    private ArrayList<ChapterListBean> downloadedSong;
    private ArrayList<AudiobookBean> downloadedBook;
    private RecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private DownloadBookListAdapter downloadBookListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_file);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (RecyclerView) findViewById(R.id.booklistview);
        myList = new ArrayList<String>();

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        if (list != null && list.length > 0) {
            downloadBookListAdapter = new DownloadBookListAdapter(this, list, this);
            listView.setAdapter(downloadBookListAdapter);
        }
        getData();
    }

    private void getData() {
        myList.clear();
        if (file.length() > 0) {
            for (File inFile : list) {
                if (inFile.isDirectory()) {
                    myList.add(inFile.getName());
                }
            }
        } else {
            Toast.makeText(this, "Downloaded file Not Found", Toast.LENGTH_SHORT).show();
        }
        if (myList != null && myList.size() > 0) {
//            DownloadBookListAdapter adpt = new DownloadBookListAdapter(DownloadFileActivity.this,list,this);
////            listView.setAdapter(adpt);
            downloadBookListAdapter.notifyDataSetChanged();
        }
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
        if (id == R.id.delete) {

            // Create custom dialog object
            final Dialog dialog = new Dialog(DownloadFileActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.custom_alert_dialog);

            TextView textView = (TextView) dialog.findViewById(R.id.msg);
            Button buttonNo = (Button) dialog.findViewById(R.id.nobtn);
            Button buttonOk = (Button) dialog.findViewById(R.id.okbtn);

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
                    if (DownloadBookListAdapter.bookPositionList != null && DownloadBookListAdapter.bookPositionList.size() > 0) {
                        for (int i = 0; i < DownloadBookListAdapter.bookPositionList.size(); i++) {
                            if (list[Integer.parseInt(DownloadBookListAdapter.bookPositionList.get(i))].isDirectory()) {
                                deleteDirectory(list[Integer.parseInt(DownloadBookListAdapter.bookPositionList.get(i))]);
//                            list[Integer.parseInt(DownloadBookListAdapter.songPositionList.get(i))].delete();
                            }

//                        list[Integer.parseInt(DownloadBookListAdapter.songPositionList.get(i))].delete();
                            downloadedBook = new ArrayList<>();
                            AudiobookBeanEntityManager audiobookBeanEntityManager = new AudiobookBeanEntityManager();

                            if (audiobookBeanEntityManager.count() > 0) {
                                downloadedBook = (ArrayList<AudiobookBean>) audiobookBeanEntityManager.select().asList();
                            }
                            for (int j = 0; j < downloadedBook.size(); j++) {
                                if (downloadedBook != null && downloadedBook.size() > 0 && downloadedBook.get(j).getAudio_book_description().equals(myList.get(i).toString())) {
                                    audiobookBeanEntityManager.delete(downloadedBook.get(j));
                                }
                            }
//                        if (list[i].getName().equals())
                        }
                        Toast.makeText(DownloadFileActivity.this, "Selected file Deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                        startActivity(getIntent());

                    }else if(DownloadChapterListAdapter.songPositionList != null && DownloadChapterListAdapter.songPositionList.size() > 0){
                        for (int i = 0; i < DownloadChapterListAdapter.songPositionList.size(); i++) {
                            if (list[Integer.parseInt(DownloadChapterListAdapter.songPositionList.get(i))].isDirectory()) {
                                deleteDirectory(list[Integer.parseInt(DownloadChapterListAdapter.songPositionList.get(i))]);
//                            list[Integer.parseInt(DownloadBookListAdapter.songPositionList.get(i))].delete();
                            }

//                        list[Integer.parseInt(DownloadBookListAdapter.songPositionList.get(i))].delete();
                            downloadedSong = new ArrayList<>();
                            ChapterListBeanEntityManager chapterListBeanEntityManager = new ChapterListBeanEntityManager();

                            if (chapterListBeanEntityManager.count() > 0) {
                                downloadedSong = (ArrayList<ChapterListBean>) chapterListBeanEntityManager.select().asList();
                            }
                            for (int j = 0; j < downloadedSong.size(); j++) {
                                if (downloadedSong != null && downloadedSong.size() > 0 && downloadedSong.get(j).getChapter_desc().equals(list[i].getName())) {
                                    chapterListBeanEntityManager.delete(downloadedSong.get(j));
                                }
                            }
//                        if (list[i].getName().equals())
                        }
                        Toast.makeText(DownloadFileActivity.this, "Selected file Deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                        startActivity(getIntent());

                    }
                }
            });

            dialog.show();


        }
        if (id == R.id.deleteAll) {

            // Create custom dialog object
            final Dialog dialog = new Dialog(DownloadFileActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.custom_alert_dialog);

            TextView textView = (TextView) dialog.findViewById(R.id.msg);
            Button buttonNo = (Button) dialog.findViewById(R.id.nobtn);
            Button buttonOk = (Button) dialog.findViewById(R.id.okbtn);

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
                    if (list != null && list.length > 0) {
                        for (int i = 0; i < list.length; i++) {
                            list[i].delete();
                            downloadedSong = new ArrayList<>();
                            ChapterListBeanEntityManager chapterListBeanEntityManager = new ChapterListBeanEntityManager();

                            if (chapterListBeanEntityManager.count() > 0) {
                                downloadedSong = (ArrayList<ChapterListBean>) chapterListBeanEntityManager.select().asList();
                            }
                            for (int j = 0; j < downloadedSong.size(); j++) {
                                if (downloadedSong != null && downloadedSong.size() > 0 && downloadedSong.get(j).getChapter_desc().equals(list[i].getName())) {
                                    chapterListBeanEntityManager.delete(downloadedSong);
                                }

                            }
                        }
                        Toast.makeText(DownloadFileActivity.this, "All file Deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                        startActivity(getIntent());
                    }
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void deleteDirectory( File dir )
    {
        if ( dir.isDirectory() )
        {
            String [] children = dir.list();
            for ( int i = 0 ; i < children.length ; i ++ )
            {
                File child =    new File( dir , children[i] );
                if(child.isDirectory()){
                    deleteDirectory( child );
                    child.delete();
                }else{
                    child.delete();

                }
            }
            dir.delete();
        }
    }

    @Override
    public void OfflineonBookClick(int pos) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.llBookList,new OfflineChapterFragment(myList.get(pos)),"");
//        transaction.addToBackStack(null);
        transaction.commit();
    }
}
