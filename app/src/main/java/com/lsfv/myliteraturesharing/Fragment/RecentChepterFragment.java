package com.lsfv.myliteraturesharing.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lsfv.myliteraturesharing.Adapter.RecentChapterListAdapter;
import com.lsfv.myliteraturesharing.Adapter.DownloadTask;
import com.lsfv.myliteraturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.myliteraturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.myliteraturesharing.Helper.Config;
import com.lsfv.myliteraturesharing.Helper.Utils;
import com.lsfv.myliteraturesharing.model.ChapterListBean;
import com.lsfv.myliteraturesharing.model.ChapterListBeanEntityManager;
import com.lsfv.myliteraturesharing.model.ChapterListModel;
import com.lsfv.myliteraturesharing.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class RecentChepterFragment extends Fragment implements RecentChapterListAdapter.ChapterClickListner {

    AVLoadingIndicatorView pvChapter;
    TextView tvLoading;
    ArrayList<ChapterListBean> downloadedSong;
    ArrayList<ChapterListBean> chapterList;
    RecentChapterListAdapter recentChapterListAdapter;
    String s, type = "r";
    SwipeRefreshLayout refreshLayout;
    private RecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private String downloadMp3Url;
    private String downloadMp3ChapterName;
    private String bkname;
    private int mFlag = 0;
    private int downloadMp3Id;
    private ArrayList<ChapterListBean> chapterDownload;
    private int temp = 0;
    final File directory = Environment.getExternalStorageDirectory();
    File file;
    File list[];
    private boolean tempsong = false;
    private int temppossong;


    public RecentChepterFragment(String bkname) {
        this.bkname = bkname;

    }

    public RecentChepterFragment(ArrayList<ChapterListBean> chapterList, String bkname) {
        this.chapterList = chapterList;
        this.bkname = bkname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_chepter, container, false);
        listView = (RecyclerView) view.findViewById(R.id.chapter_list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        pvChapter = (AVLoadingIndicatorView) view.findViewById(R.id.pv_chapter);
        tvLoading = (TextView) view.findViewById(R.id.tv_loading);

        chapterList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
            recentChapterListAdapter = new RecentChapterListAdapter(getActivity(), chapterList, this);
            listView.setAdapter(recentChapterListAdapter);
        Swipe();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 3000);
                Swipe();
            }
        });
        return view;
    }

    private void Swipe() {
        pvChapter.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        chapterList.clear();
        SharedPreferences preferences = getActivity().getSharedPreferences("bookid", Context.MODE_PRIVATE);
        s = preferences.getString("id", null);
        String[] keys = new String[]{"mode", "audio_book_id", "file_recent"};
        String[] values = new String[]{"getchapter", s, "r"};
        String jsonRequest = Utils.createJsonRequest(keys, values);

        String URL = Config.MAIN_URL;
        new WebserviceCall(getContext(), URL, jsonRequest, "Getting chapterList...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("myapp", response);
                final ChapterListModel model = new Gson().fromJson(response, ChapterListModel.class);
                if (model.getStatus().equalsIgnoreCase("1")) {
                    tvLoading.setVisibility(View.GONE);
                    pvChapter.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    chapterList.addAll(model.getChapterList());
                    recentChapterListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String message) {
                pvChapter.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
            }
        }).execute();

    }

    @Override
    public void onChapterClick(int pos) {
        file = new File(directory + "/" + ".Literature sharing for VI" + "/"+""+bkname+""+"/");
        list = file.listFiles();
        PackageManager pm = getActivity().getPackageManager();
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent,0);
        for (int i = 0; i <resInfo.size()  ; i++) {
            ResolveInfo ri = resInfo.get(i);
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

        ChapterListBeanEntityManager chapterListBeanEntityManager = new ChapterListBeanEntityManager();
        chapterDownload = new ArrayList<>();
        if (chapterListBeanEntityManager.count() > 0) {
            chapterDownload = (ArrayList<ChapterListBean>) chapterListBeanEntityManager.select().asList();
        }
        for (int i = 0; i < chapterDownload.size() ; i++) {
            for (int j = 0; j < chapterList.size() ; j++) {
                if (chapterDownload.get(i).getChapter_id() == (chapterList.get(j).getChapter_id())){
                    for (int k = 0; k < list.length; k++) {
                        if (chapterList.get(pos).getChapter_desc().equals(list[k].getName())){
                            tempsong = true;
                            temppossong = k;
                        }}}}}

        if (tempsong){
            Toast.makeText(getActivity(), "Playing offline", Toast.LENGTH_SHORT).show();
            File file = new File(list[temppossong].getAbsolutePath());
            Uri apkURI = FileProvider.getUriForFile(
                    getActivity(),
                    getActivity().getApplicationContext()
                            .getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, "audio/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().startActivity(intent);
        }else {
            Toast.makeText(getActivity(), "Playing online..", Toast.LENGTH_SHORT).show();
            intent.setDataAndType(Uri.parse(chapterList.get(pos).getChapter_file()), "audio/*");
            startActivity(intent);
        }
    }

    @Override
    public void onDownloadClick(int pos) {
        ChapterListBeanEntityManager chapterListBeanEntityManager = new ChapterListBeanEntityManager();
        downloadedSong = new ArrayList<>();
        if (chapterListBeanEntityManager.count() > 0) {
            chapterDownload = (ArrayList<ChapterListBean>) chapterListBeanEntityManager.select().asList();
        }
        if (chapterDownload != null && chapterDownload.size() > 0) {
            for (int i = 0; i < chapterDownload.size(); i++) {
                if (chapterDownload.get(i).getChapter_id() == chapterList.get(pos).getChapter_id()
                        && chapterDownload.get(i).getChapter_desc().equals(chapterList.get(pos).getChapter_desc())) {
                    temp = 1;
                }
            }
        }
        if (temp == 0) {
            downloadMp3Id = chapterList.get(pos).getChapter_id();
            downloadMp3Url = chapterList.get(pos).getChapter_file();
            downloadMp3ChapterName = chapterList.get(pos).getChapter_desc();
            new DownloadTask(getActivity(), downloadMp3Id, downloadMp3Url, downloadMp3ChapterName, bkname);
        }else{
            Toast.makeText(getActivity(), "Already Downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}
