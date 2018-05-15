package com.lsfv.myliteraturesharing.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.lsfv.myliteraturesharing.R;

import java.io.File;
import java.util.ArrayList;


public class DownloadBookListAdapter extends RecyclerView.Adapter<DownloadBookListAdapter.ViewHolder>{

    Context context;
    int layout;
    public static ArrayList<String> bookPositionList = new ArrayList<>();
    //    List<String> myList;
    LayoutInflater inflater;
    ArrayList<String> arrayList = new ArrayList<>();
    File fileList[];
    private OfflineBookClickListner offlineBookClickListner;

    public DownloadBookListAdapter(Context context, File[] list, OfflineBookClickListner offlineBookListener) {
        this.context = context;
        this.fileList = list;
        this.offlineBookClickListner = offlineBookListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_downloadlist_layout,parent,false);
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
                if (offlineBookClickListner != null){
                    int pos = (int) v.getTag();
                    offlineBookClickListner.OfflineonBookClick(pos);
                }
            }else if (v == cb){
                int pos = (int) v.getTag();
                if (cb.isChecked()) {
                    bookPositionList.add(String.valueOf(pos));
                } else {
                    bookPositionList.remove(pos);
                }
            }
        }
    }

        public interface OfflineBookClickListner {
        void OfflineonBookClick(int pos);
    }

}

//public class DownloadChepterList extends BaseAdapter {
//
//    Context context;
//    int layout;
//    public static ArrayList<String> songPositionList = new ArrayList<>();
//    //    List<String> myList;
//    LayoutInflater inflater;
//    ArrayList<String> arrayList = new ArrayList<>();
//    File fileList[];
//
//    public DownloadChepterList(Context context, int custom_downloadlist_layout, List<String> myList) {
//        this.context = context;
//        this.layout = custom_downloadlist_layout;
////    this.myList=myList;
//    }
//
//    public DownloadChepterList(Context context, int custom_downloadlist_layout, File[] list) {
//        this.context = context;
//        this.layout = custom_downloadlist_layout;
//        fileList = list;
//    }
//
//    @Override
//    public int getCount() {
//        return fileList.length;
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return i;
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(final int i, View view, ViewGroup viewGroup) {
//        view = inflater.from(context).inflate(layout, null);
//        TextView tv = (TextView) view.findViewById(R.id.sg_name);
//        final CheckBox cb = (CheckBox) view.findViewById(R.id.chk);
//
//        tv.setText(fileList[i].getName());
//
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                // new update
////                Intent intent = new Intent();
////                intent.setAction(android.content.Intent.ACTION_VIEW);
////                intent.setPackage("com.mxtech.videoplayer.pro");
////
////                File file = new File(fileList[i].getAbsolutePath());
////                Uri apkURI = FileProvider.getUriForFile(
////                        context,
////                        context.getApplicationContext()
////                                .getPackageName() + ".provider", file);
////
////                intent.setDataAndType(apkURI, "audio/*");
////
////                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                context.startActivity(intent);
//
//                // try to play song only mxplayer
////                Resources resources = context.getResources();
////
////                Intent emailIntent = new Intent();
////                emailIntent.setAction(Intent.ACTION_VIEW);
////                File file = new File(fileList[i].getAbsolutePath());
////                Uri apkURI = FileProvider.getUriForFile(
////                        context,
////                        context.getApplicationContext()
////                                .getPackageName() + ".provider", file);
////
////                emailIntent.setDataAndType(apkURI, "audio/*");
////
////                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                PackageManager pm = context.getPackageManager();
////
////
////                Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_chooser_text));
////
////                List<ResolveInfo> resInfo = pm.queryIntentActivities(emailIntent, 0);
////                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
////                for (int i = 0; i < resInfo.size(); i++) {
////                     Extract the label, append it, and repackage it in a LabeledIntent
////                    ResolveInfo ri = resInfo.get(0);
////                    String packageName = ri.activityInfo.packageName;
////                    if(packageName.contains("com.mxtech.videoplayer.ad")) {
////                        Intent intent = new Intent();
////                        intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
////                        File file1 = new File(fileList[i].getAbsolutePath());
////                        Uri apkURI1 = FileProvider.getUriForFile(
////                                context,
////                                context.getApplicationContext()
////                                        .getPackageName() + ".provider", file1);
////
////                        intent.setDataAndType(apkURI1, "audio/*");
////                        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_mxplayer));
////                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                        intent.setAction(Intent.ACTION_VIEW);
////
////                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
////                    }
////                }
////
////                 convert intentList to array
////                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);
////
////                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
////                context.startActivity(openInChooser);
//            }
//        });
//
//        cb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (cb.isChecked()) {
//                    songPositionList.add(String.valueOf(i));
//                } else {
//                    songPositionList.remove(i);
//                }
//            }
//        });
//        return view;
//    }
//
//
//    public interface OfflineBookClickListner {
//        void OfflineonBookClick(int pos);
//    }
//}