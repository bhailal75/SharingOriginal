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
import android.widget.ListView;

import com.google.gson.Gson;
import com.lsfv.literaturesharing.Adapter.ChapterListAdapter;
import com.lsfv.literaturesharing.Adapter.DownloadTask;
import com.lsfv.literaturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.literaturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.literaturesharing.Helper.Config;
import com.lsfv.literaturesharing.Helper.Utils;
import com.lsfv.literaturesharing.Model.ChapterListBean;
import com.lsfv.literaturesharing.Model.ChapterListModel;
import com.lsfv.literaturesharing.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class AllChapterFragment extends Fragment implements ChapterListAdapter.ChapterClickListner {
//    ListView listView;

    ArrayList<ChapterListBean> chapterList;
    MediaPlayer player;
    String s,type="a";
    ChapterListAdapter chapterListAdapter;
    SwipeRefreshLayout refreshLayout;
    private RecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private String downloadMp3Url;
    private String downloadMp3ChapterName;


    public AllChapterFragment() {
        // Required empty public constructor
    }

    public AllChapterFragment(ArrayList<ChapterListBean> chapterList) {
        this.chapterList = chapterList;

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_all_chapter, container, false);
        listView= (RecyclerView) view.findViewById(R.id.chapter_list);
        refreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);

        Swipe();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        chapterListAdapter = new ChapterListAdapter(getActivity(), chapterList, this);
        listView.setAdapter(chapterListAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 500);
                Swipe();
            }
        });

        return view;
    }

    private void Swipe() {

        SharedPreferences preferences=getActivity().getSharedPreferences("bookid", Context.MODE_PRIVATE);
        s=preferences.getString("id",null);

        String[]keys=new String[]{"mode","audio_book_id","file_recent"};
        String[]values=new String[]{"getchapter",s,"a"};
        String jsonRequest= Utils.createJsonRequest(keys,values);
        Log.d("chapter",jsonRequest);
        String URL = Config.MAIN_URL;
        new WebserviceCall(getContext(), URL, jsonRequest, "Getting chapterList...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("myapp1",response);
                final ChapterListModel model = new Gson().fromJson(response,ChapterListModel.class);
               //Toast.makeText(getContext(),"Refreshing" , Toast.LENGTH_SHORT).show();
                if (model.getStatus().equalsIgnoreCase("1"))
                {
                    chapterList.clear();
//                    chapterList=new ArrayList<>();
                    for (int i=0;i<model.getChapterList().size();i++)
                    {
                        chapterList.add(model.getChapterList().get(i));
                    }
                    chapterListAdapter.notifyDataSetChanged();
//                    chapterListAdapter=new ChapterListAdapter(getContext(),R.layout.chapterlist_cus_layout,chapterList,type);
//                    listView.setAdapter(chapterListAdapter);
                }
            }

            @Override
            public void onFailure(String message) {
               // Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public void onChapterClick(int pos) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setDataAndType(Uri.parse(chapterList.get(pos).getChapter_file()), "audio/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, chapterList);
        startActivity(intent);

    }

    @Override
    public void onDownloadClick(int pos) {

        downloadMp3Url=chapterList.get(pos).getChapter_file();
        downloadMp3ChapterName=chapterList.get(pos).getChapter_desc();
//                //Toast.makeText(context, ""+downloadMp3Url, Toast.LENGTH_SHORT).show();

        new DownloadTask(getActivity(),downloadMp3Url,downloadMp3ChapterName);
    }
}
