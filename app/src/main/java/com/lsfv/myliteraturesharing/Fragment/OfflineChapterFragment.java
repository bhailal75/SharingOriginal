package com.lsfv.myliteraturesharing.Fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lsfv.myliteraturesharing.Adapter.DownloadChapterListAdapter;
import com.lsfv.myliteraturesharing.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sony on 04-05-2018.
 */

@SuppressLint("ValidFragment")
public class OfflineChapterFragment extends Fragment implements DownloadChapterListAdapter.OfflineChapterClickListner {

    private RecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private DownloadChapterListAdapter downloadChapterListAdapter;
    private List<String> myList;
    String bkname;
    //    ListView listView;
    final File directory = Environment.getExternalStorageDirectory();
    File file;
    File list[];
    private int mFlag=0;

    public OfflineChapterFragment(){}

    public OfflineChapterFragment(String s) {
        this.bkname = s;
        file = new File(directory + "/" + ".Literature sharing for VI" + "/"+""+bkname+""+"/");
        list = file.listFiles();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_offline_chapter, container, false);
        listView= (RecyclerView) view.findViewById(R.id.songlistview);
        myList = new ArrayList<String>();

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);

        getData();
        return view;
    }
    private void getData() {
        myList.clear();
        if (file.length() > 0) {
            for (File inFile : list) {
                myList.add(inFile.getName());
            }
        } else {
            Toast.makeText(getActivity(), "Downloaded file Not Found", Toast.LENGTH_SHORT).show();
        }
        if (myList != null && myList.size() > 0) {
        downloadChapterListAdapter = new DownloadChapterListAdapter(getActivity(), list, this,bkname);
        listView.setAdapter(downloadChapterListAdapter);
        }
    }

        @Override
    public void OfflineChapterClick(int i) {
            //                // new update
            PackageManager pm = getActivity().getPackageManager();
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            List<ResolveInfo> resInfo = pm.queryIntentActivities(intent,0);
            for (int j = 0; j <resInfo.size()  ; j++) {
                ResolveInfo ri = resInfo.get(j);
                String packageName = ri.activityInfo.packageName;
                if (packageName.equals("com.mxtech.videoplayer.pro")){
                    mFlag = 1;
                }else if (packageName.equals("com.mxtech.videoplayer.ad")){
                    mFlag = 2;
                }else{
                }
            }
            if (mFlag == 1){
                intent.setPackage("com.mxtech.videoplayer.pro");
            }else if (mFlag ==2){
                intent.setPackage("com.mxtech.videoplayer.ad");
            }else{
                Toast.makeText(getActivity(), "Please download MxPlayer", Toast.LENGTH_SHORT).show();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.mxtech.videoplayer.ad")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.mxtech.videoplayer.ad")));
                }

            }
            File file = new File(list[i].getAbsolutePath());
                Uri apkURI = FileProvider.getUriForFile(
                        getActivity(),
                        getActivity().getApplicationContext()
                                .getPackageName() + ".provider", file);
                intent.setDataAndType(apkURI, "audio/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getActivity().startActivity(intent);
        }
}
