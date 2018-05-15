package com.lsfv.myliteraturesharing.Adapter;

import android.app.NotificationManager;
import android.app.ProgressDialog;
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
import java.util.ArrayList;

public class DownloadBookTask {
    private String bkname;
    static float counter = 0;

    ArrayList<String> path = new ArrayList<>();
    public static final String mainUrl = "http://www.lsfv.in/admin/chapters/";
    public static final String downloadDirectory = ".Literature sharing for VI";
    private ArrayList<ChapterListBean> chapterList;
    private String downloadUrl = "", downloadFileName = "", downloadchaptername = "";
    Context context;

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

    public DownloadBookTask(Context context, ArrayList<ChapterListBean> chapterList,String  bkname) {
        this.context = context;
        this.chapterList = chapterList;
        this.bkname = bkname;

        for (int i = 0; i < chapterList.size(); i++) {
            path.add(chapterList.get(i).chapter_file);
        }
//        Toast.makeText(context, "Downloading " +bkname, Toast.LENGTH_SHORT).show();
        DownloadingTask downloadingTask = new DownloadingTask(path);
        downloadingTask.execute(path);
    }

    //update
    public void notificationDisplay() {
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(""+bkname).setContentText("Downloading Book...").setSmallIcon(R.drawable.ic_file_download);
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
    private class DownloadingTask extends AsyncTask<ArrayList<String>, Integer, String> {
        ProgressDialog dialog;
        File apkStorage = null;
        File outputFile = null;
        int current = 0;
        private ArrayList<String> path = new ArrayList<>();

        public DownloadingTask(ArrayList<String> path) {
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            notificationDisplay();
        }

        @Override
        protected String doInBackground(ArrayList<String>... fileurl) {
            int raw = fileurl.length;
            for (current = 0; current <= path.size(); current++){
                try {
                    URL url = new URL(path.get(current));//Create Download URl
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                    c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                    c.connect();//connect the URL Connection

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
                    outputFile = new File(apkStorage, chapterList.get(current).getChapter_desc());//Create Output file in Main File
                    //Create New File if not present
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                        ChapterListBeanEntityManager chapterListBeanEntityManager = new ChapterListBeanEntityManager();
                        ChapterListBean chapterListBean = new ChapterListBean();
                        chapterListBean.setChapter_id(chapterList.get(current).getChapter_id());
                        chapterListBean.setChapter_desc(chapterList.get(current).getChapter_desc());
                        chapterListBean.setChapter_file(chapterList.get(current).getChapter_file());
                        chapterListBean.setDuration(chapterList.get(current).getDuration());
                        chapterListBeanEntityManager.add(chapterListBean);

                        fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                        is = c.getInputStream();//Get InputStream for connection

                        byte[] buffer = new byte[1024];//Set buffer type
                        int len1 = 0;//init length
                        while ((len1 = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);//Write new file
                        }
                    }
                    fos.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    outputFile = null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            int len = path.size();
            if (counter >= len-1){
                mBuilder.setContentTitle("Downloading "+bkname+" completed.");
                mBuilder.setContentText("Downloading done")
                        .setProgress(0,0,false);
                mNotifyManager.notify(id,mBuilder.build());
//                Toast.makeText(context, "Downloading " +bkname +" completed", Toast.LENGTH_SHORT).show();
            }else{
                int per = (int) (((counter + 1) / len) * 100f);
                Log.i("Counter", "Counter : " + counter + ", per : " + per);
                mBuilder.setContentText("Downloaded (" + per + "%"+"/100%");
                mBuilder.setProgress(100, per, false);
                mNotifyManager.notify(id, mBuilder.build());
                counter++;
            }
        }
    }
}
