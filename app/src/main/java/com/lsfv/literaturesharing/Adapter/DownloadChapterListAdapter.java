package com.lsfv.literaturesharing.Adapter;

import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.lsfv.literaturesharing.R;

import java.io.File;
import java.util.ArrayList;

public class DownloadChapterListAdapter extends RecyclerView.Adapter<DownloadChapterListAdapter.ViewHolder>{

    Context context;
    int layout;
    public static ArrayList<String> songPositionList = new ArrayList<>();
    //    List<String> myList;
    LayoutInflater inflater;
    ArrayList<String> arrayList = new ArrayList<>();
    File fileList[];
    private OfflineChapterClickListner OfflineChapterClick;

    public DownloadChapterListAdapter(Context context, File[] list, OfflineChapterClickListner OfflineChapterClick) {
        this.context = context;
        this.fileList = list;
        this.OfflineChapterClick = OfflineChapterClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_download_chapter_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText(fileList[position].getName());
        holder.tv.setTag(position);
        holder.cb.setTag(position);
    }

    @Override
    public int getItemCount() {
        return fileList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv;
        CheckBox cb;
        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.sg_name);
            cb = (CheckBox) itemView.findViewById(R.id.chk);
            tv.setOnClickListener(this);
            cb.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == tv){
                if (OfflineChapterClick != null){
                    int pos = (int) v.getTag();
                    OfflineChapterClick.OfflineChapterClick(pos);
                }
            }else if (v == cb){
                int pos = (int) v.getTag();
                if (cb.isChecked()) {
                    songPositionList.add(String.valueOf(pos));
                } else {
                    songPositionList.remove(pos);
                }
            }
        }
    }

        public interface OfflineChapterClickListner {
        void OfflineChapterClick(int pos);
    }

}