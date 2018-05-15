package com.lsfv.myliteraturesharing.Adapter;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import com.lsfv.myliteraturesharing.Helper.CheckForSDCard;
import com.lsfv.myliteraturesharing.Notification.NLService;
import com.lsfv.myliteraturesharing.R;
import com.lsfv.myliteraturesharing.model.ChapterListBean;
import com.lsfv.myliteraturesharing.model.ChapterListBeanEntityManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {
    public static final String mainUrl = "http://www.lsfv.in/admin/chapters/";
    public static final String downloadDirectory = ".Literature sharing for VI";
    private final int downloadMp3Id;
    private String downloadUrl = "", downloadFileName = "",downloadchaptername="";
    Context context;
    String bkname;

    //update
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;
    int id = 1;

    public NotificationReceiver nReceiver;
    private InputStream is;
    private FileOutputStream fos;


    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getExtras().getString(NLService.NOT_EVENT_KEY);
            Log.i("NotificationReceiver", "NotificationReceiver onReceive : " + event);
            if (event.trim().contentEquals(NLService.NOT_REMOVED)) {
                killTasks();
            }
        }
    }

    private void killTasks() {
        mNotifyManager.cancelAll();
    }

    public DownloadTask(Context context,int downloadMp3Id, String downloadMp3Url, String downloadchaptername, String bkname) {
        this.context=context;
        this.downloadMp3Id= downloadMp3Id;
        this.downloadUrl=downloadMp3Url;
        this.downloadchaptername=downloadchaptername;
        downloadFileName = downloadchaptername+""+downloadUrl.replace(mainUrl,"");
        downloadFileName = downloadchaptername;//Create file name by picking download file name from URL
        Log.e("file name", downloadFileName);
        this.bkname = bkname;
//        Toast.makeText(context, "Downloading " + downloadchaptername, Toast.LENGTH_SHORT).show();

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    //update
    public void notificationDisplay() {
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(""+downloadchaptername).setContentText("Downloading chapter...").setSmallIcon(R.drawable.ic_file_download);
        // Start a lengthy operation in a background thread
        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(id, mBuilder.build());
        mBuilder.setAutoCancel(true);
        ContentResolver contentResolver = context.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = context.getPackageName();

        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            context.startActivity(intent);
        } else {
            Log.i("ACC", "Have Notification access");
        }

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NLService.NOT_TAG);
        context.registerReceiver(nReceiver, filter);
    }

    private class DownloadingTask extends AsyncTask<Void,Void,Void> {
        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            notificationDisplay();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("UrlFinal",downloadUrl);
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("h", "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }

                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) {

                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + downloadDirectory + "/"
                                    + bkname);
                } else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdirs();
                    Log.e("g", "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                    InputStream is = c.getInputStream();//Get InputStream for connection
                    byte[] buffer = new byte[1024];//Set buffer type
                    int len1 = 0;//init length
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);//Write new file
                    }
                    ChapterListBeanEntityManager chapterListBeanEntityManager = new ChapterListBeanEntityManager();
                    ChapterListBean chapterListBean = new ChapterListBean();
                    chapterListBean.setChapter_id(downloadMp3Id);
                    chapterListBean.setChapter_desc(downloadchaptername);
                    chapterListBean.setChapter_file(downloadFileName);
                    chapterListBean.setDuration("0");
                    chapterListBeanEntityManager.add(chapterListBean);
                }

                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e("a", "Download Error Exception " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                mBuilder.setContentTitle("Downloading "+downloadchaptername+" completed.");
                mBuilder.setContentText("Downloading done")
                        .setProgress(0,0,false);
                mNotifyManager.notify(id,mBuilder.build());
//            Toast.makeText(context, "Downloading " +downloadchaptername +" completed.", Toast.LENGTH_SHORT).show();
        }
    }
}
