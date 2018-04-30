package com.lsfv.literaturesharing.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.lsfv.literaturesharing.OfflinePlayerScreenActivity;
import com.lsfv.literaturesharing.R;

import java.util.ArrayList;
import java.util.List;



public class DownloadChepterList extends BaseAdapter {

    Context context;
    int layout;
    public static ArrayList<String> songPositionList =new ArrayList<>();
    List<String> myList;
    LayoutInflater inflater;
    ArrayList<String>arrayList=new ArrayList<>();

    public DownloadChepterList(Context context, int custom_downloadlist_layout, List<String> myList) {
    this.context=context;
    this.layout=custom_downloadlist_layout;
    this.myList=myList;
    }

    @Override
    public int getCount() {
        return myList.size();
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

        tv.setText(myList.get(i));

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, OfflinePlayerScreenActivity.class);
                intent.putExtra("position",i);
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
