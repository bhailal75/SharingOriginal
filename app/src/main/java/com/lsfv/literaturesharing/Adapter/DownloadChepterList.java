package com.lsfv.literaturesharing.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.lsfv.literaturesharing.DownloadFileActivity;
import com.lsfv.literaturesharing.OfflinePlayerScreenActivity;
import com.lsfv.literaturesharing.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class DownloadChepterList extends BaseAdapter {

    Context context;
    int layout;
    public static ArrayList<String> songPositionList =new ArrayList<>();
//    List<String> myList;
    LayoutInflater inflater;
    ArrayList<String>arrayList=new ArrayList<>();
    File fileList[] ;
    public DownloadChepterList(Context context, int custom_downloadlist_layout, List<String> myList) {
    this.context=context;
    this.layout=custom_downloadlist_layout;
//    this.myList=myList;
    }

    public DownloadChepterList(Context context, int custom_downloadlist_layout, File[] list) {
        this.context=context;
        this.layout=custom_downloadlist_layout;
        fileList = list;
    }

    @Override
    public int getCount() {
        return fileList.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view=inflater.from(context).inflate(layout,null);
        TextView tv=(TextView)view.findViewById(R.id.sg_name);
        final CheckBox cb=(CheckBox)view.findViewById(R.id.chk);

        tv.setText(fileList[i].getName());

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(context, OfflinePlayerScreenActivity.class);
//                intent.putExtra("position",i);
//                context.startActivity(intent);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);

                File file = new File(fileList[i].getAbsolutePath());
                Uri apkURI = FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext()
                                .getPackageName() + ".provider", file);

                intent.setDataAndType(apkURI, "audio/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        });

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (cb.isChecked()){
                   songPositionList.add(String.valueOf(i));
               }else {
                   songPositionList.remove(i);
               }
            }
        });
        return view;
    }
}
