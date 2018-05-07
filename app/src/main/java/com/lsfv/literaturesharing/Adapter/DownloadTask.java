package com.lsfv.literaturesharing.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.lsfv.literaturesharing.Helper.CheckForSDCard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class DownloadTask {
    public static final String mainUrl = "http://www.lsfv.in/admin/chapters/";
    public static final String downloadDirectory = "Literature sharing for VI";
    private String downloadUrl = "", downloadFileName = "",downloadchaptername="";
    Context context;
    String bkname;
    public DownloadTask(Context context, String downloadMp3Url, String downloadchaptername, String bkname) {
        this.context=context;
        this.downloadUrl=downloadMp3Url;
        this.downloadchaptername=downloadchaptername;
        downloadFileName = downloadchaptername+""+downloadUrl.replace(mainUrl,"");
        downloadFileName = downloadchaptername;//Create file name by picking download file name from URL
        Log.e("file name", downloadFileName);
        this.bkname = bkname;

        //Start Downloading Task
        new DownloadingTask().execute();
    }


    private class DownloadingTask extends AsyncTask<Void,Void,Void> {
        ProgressDialog dialog;
        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
           // Toast.makeText(context, ""+downloadUrl, Toast.LENGTH_SHORT).show();
            super.onPreExecute();
            dialog=new ProgressDialog(context);
            dialog.setMessage("Downloading");
            dialog.setTitle("Download");
            dialog.show();
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
                    Log.e("d", "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
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

            if (outputFile != null) {
                Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();

            }
            dialog.dismiss();
        }
    }
}
