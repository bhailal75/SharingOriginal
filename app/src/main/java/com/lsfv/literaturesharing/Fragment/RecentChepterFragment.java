package com.lsfv.literaturesharing.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lsfv.literaturesharing.Adapter.RecentChapterListAdapter;
import com.lsfv.literaturesharing.Adapter.DownloadTask;
import com.lsfv.literaturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.literaturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.literaturesharing.Helper.Config;
import com.lsfv.literaturesharing.Helper.Utils;
import com.lsfv.literaturesharing.model.ChapterListBean;
import com.lsfv.literaturesharing.model.ChapterListModel;
import com.lsfv.literaturesharing.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class RecentChepterFragment extends Fragment implements RecentChapterListAdapter.ChapterClickListner {

    AVLoadingIndicatorView pvChapter;
    TextView tvLoading;
    ArrayList<ChapterListBean> downloadedSong;
//    ListView listView;
    ArrayList<ChapterListBean> chapterList;
    MediaPlayer player;
    RecentChapterListAdapter recentChapterListAdapter;
    String s,type="r";
    SwipeRefreshLayout refreshLayout;
    private RecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private String downloadMp3Url;
    private String downloadMp3ChapterName;
    private String bkname;
    public RecentChepterFragment() {
        // Required empty public constructor
    }

    public RecentChepterFragment(ArrayList<ChapterListBean> chapterList, String bkname) {
        this.chapterList = chapterList;
        this.bkname = bkname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_recent_chepter, container, false);
        listView= (RecyclerView) view.findViewById(R.id.chapter_list);
        refreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        pvChapter = (AVLoadingIndicatorView) view.findViewById(R.id.pv_chapter);
        tvLoading = (TextView) view.findViewById(R.id.tv_loading);


//        chapterList = new ArrayList<>();


        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        recentChapterListAdapter = new RecentChapterListAdapter(getActivity(), chapterList, this);
        listView.setAdapter(recentChapterListAdapter);

//        Swipe();
//        recentChapterListAdapter=new RecentChapterListAdapter(getContext(),R.layout.chapterlist_cus_layout,chapterList,type);
//        listView.setAdapter(recentChapterListAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
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
        SharedPreferences preferences=getActivity().getSharedPreferences("bookid", Context.MODE_PRIVATE);
        s=preferences.getString("id",null);
        String[]keys=new String[]{"mode","audio_book_id","file_recent"};
        String[]values=new String[]{"getchapter",s,"r"};
        String jsonRequest= Utils.createJsonRequest(keys,values);

        String URL = Config.MAIN_URL;
        new WebserviceCall(getContext(), URL, jsonRequest, "Getting chapterList...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("myapp",response);
                final ChapterListModel model = new Gson().fromJson(response,ChapterListModel.class);
//                Toast.makeText(getContext(),"Refreshing" , Toast.LENGTH_SHORT).show();
                if (model.getStatus().equalsIgnoreCase("1"))
                {
                    chapterList.clear();
                    tvLoading.setVisibility(View.GONE);
                    pvChapter.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    chapterList.addAll(model.getChapterList());
//                    for (int i=0;i<model.getChapterList().size();i++)
//                    {
//                        chapterList.add(model.getChapterList().get(i));
//                    }

                    recentChapterListAdapter.notifyDataSetChanged();
//                    recentChapterListAdapter=new RecentChapterListAdapter(getContext(),R.layout.chapterlist_cus_layout,chapterList,type);
//                    listView.setAdapter(recentChapterListAdapter);
                }
            }

            @Override
            public void onFailure(String message) {
                pvChapter.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
               // Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }

    @Override
    public void onChapterClick(int pos) {
              Intent intent = new Intent();
              intent.setAction(android.content.Intent.ACTION_VIEW);
              intent.setDataAndType(Uri.parse(chapterList.get(pos).getChapter_file()), "audio/*");
              startActivity(intent);
    }

    @Override
    public void onDownloadClick(int pos) {

            downloadMp3Url=chapterList.get(pos).getChapter_file();
            downloadMp3ChapterName=chapterList.get(pos).getChapter_desc();
//                //Toast.makeText(context, ""+downloadMp3Url, Toast.LENGTH_SHORT).show();

                new DownloadTask(getActivity(),downloadMp3Url,downloadMp3ChapterName,bkname);
    }
}
