package com.lsfv.literaturesharing.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lsfv.literaturesharing.model.ChapterListBean;
import com.lsfv.literaturesharing.R;
import com.lsfv.literaturesharing.model.ChapterListBeanEntityManager;

import java.util.ArrayList;
import java.util.List;

public class AllChapterListAdapter extends RecyclerView.Adapter<AllChapterListAdapter.ViewHolder>{

    private ArrayList<ChapterListBean> downloadedSong;
    private Context context;
    private List<ChapterListBean> audioChapterList;
    private ChapterClickListner chapterClickListner;


    public AllChapterListAdapter(Context context, ArrayList<ChapterListBean> audioChapterList, ChapterClickListner chapterClickListner) {
        this.context = context;
        this.audioChapterList = audioChapterList;
        this.chapterClickListner = chapterClickListner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapterlist_cus_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChapterListBean chapterModel = audioChapterList.get(position);
        for (int i = 0; i < downloadedSong.size(); i++) {
            if (downloadedSong != null && downloadedSong.size() > 0 && downloadedSong.get(i).getChapter_id() == audioChapterList.get(position).getChapter_id()){
                holder.download.setVisibility(View.GONE);
            }
        }

        holder.textView.setText(chapterModel.getChapter_desc().toString());
        holder.download.setTag(position);
        holder.rvBookRaw.setTag(position);
    }

    @Override
    public int getItemCount() {
        return audioChapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        RelativeLayout rvBookRaw;
        Button download;


        public ViewHolder(View itemView) {
            super(itemView);

            textView=(TextView)itemView.findViewById(R.id.chapter_name);
            download=(Button)itemView.findViewById(R.id.download_btn);
            rvBookRaw = (RelativeLayout) itemView.findViewById(R.id.book_Raw);

            downloadedSong = new ArrayList<>();
            ChapterListBeanEntityManager chapterListBeanEntityManager = new ChapterListBeanEntityManager();
            if (chapterListBeanEntityManager.count() > 0){
                downloadedSong = (ArrayList<ChapterListBean>) chapterListBeanEntityManager.select().asList();
            }

            rvBookRaw.setOnClickListener(this);
            download.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (view == download){
                if (chapterClickListner != null) {
                    int pos = (int) view.getTag();
                    ChapterListBeanEntityManager chapterListBeanEntityManager = new ChapterListBeanEntityManager();
                    ChapterListBean chapterListBean = new ChapterListBean();
                    chapterListBean.setChapter_id(audioChapterList.get(pos).getChapter_id());
                    chapterListBean.setChapter_desc(audioChapterList.get(pos).getChapter_desc());
                    chapterListBean.setChapter_file(audioChapterList.get(pos).getChapter_file());
                    chapterListBean.setDuration(audioChapterList.get(pos).getDuration());
                    chapterListBeanEntityManager.add(chapterListBean);
                    download.setVisibility(View.GONE);
                    chapterClickListner.onDownloadClick(pos);


                }
            }
            else if(chapterClickListner != null) {
                int pos = (int) view.getTag();
                chapterClickListner.onChapterClick(pos);
            }
        }
    }

    public interface ChapterClickListner {
        void onChapterClick(int pos);
        void onDownloadClick(int pos);
    }
}




//public class RecentChapterListAdapter extends BaseAdapter {
//    public static String downloadMp3Url = null;
//    public static String downloadMp3ChapterName = null;
//    Context context;
//    int layout;
//    String type;
//    ArrayList<ChapterListBean> chapterList;
//    LayoutInflater inflater;
//    MediaPlayer player =new MediaPlayer();
//    public RecentChapterListAdapter(Context context, int booklist_cus_layout, ArrayList<ChapterListBean> book, String type) {
//        this.context=context;
//        this.layout=booklist_cus_layout;
//        this.chapterList=book;
//        this.type=type;
//    }
//
//
//    @Override
//    public int getCount() {
//        return chapterList.size();
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
//        final ViewHolder holder=new ViewHolder();
//        view=inflater.from(context).inflate(layout,null);
//        //holder.play_btn=(ImageView)view.findViewById(R.id.play_btn);
//        //holder.pause_btn=(ImageView)view.findViewById(R.id.pause_btn);
//        holder.textView=(TextView)view.findViewById(R.id.chapter_name);
//        holder.download=(Button) view.findViewById(R.id.download_btn);
//
//        holder.textView.setText(chapterList.get(i).getChapter_desc().toString());
//
//        holder.textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //perticular app open
////                Intent intent = context.getPackageManager().getLaunchIntentForPackage("org.videolan.vlc");
////                if (intent != null) {
////                    // We found the activity now start the activity
////                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//////                    intent.setPackage("org.videolan.vlc");
//////                    intent.setAction(Intent.ACTION_VIEW);
//////                    intent.setDataAndType(Uri.parse(chapterList.get(i).getChapter_file()), "audio/*");
////                    context.startActivity(intent);
////                } else {
////                    // Bring user to the market or let them choose an app?
////                    intent = new Intent(Intent.ACTION_VIEW);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    intent.setData(Uri.parse("market://details?id=" + "org.videolan.vlc"));
////                    context.startActivity(intent);
////                }
//
//
//                //share intent
//                Intent intent = new Intent();
//                intent.setAction(android.content.Intent.ACTION_VIEW);
//
//                intent.setDataAndType(Uri.parse(chapterList.get(i).getChapter_file()), "audio/*");
//                context.startActivity(intent);
//            // original
////                Intent intent=new Intent(context, PlayerScreenActivity.class);
////                int a=i;
////                intent.putExtra("link",chapterList.get(i).getChapter_file());
////                intent.putExtra("position",a);
////                intent.putExtra("name",chapterList.get(i).getChapter_desc());
////                intent.putExtra("duration",chapterList.get(i).getDuration());
////                intent.putExtra("type",type);
////                context.startActivity(intent);
//            }
//        });
//
//        holder.download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                downloadMp3Url=chapterList.get(i).getChapter_file();
//                downloadMp3ChapterName=chapterList.get(i).getChapter_desc();
//                //Toast.makeText(context, ""+downloadMp3Url, Toast.LENGTH_SHORT).show();
//                new DownloadTask(context,downloadMp3Url,downloadMp3ChapterName);
//            }
//        });
//
//        return view;
//    }
//
//
//    class ViewHolder {
//        ImageView play_btn,pause_btn;
//        Button download;
//        TextView textView;
//    }
//}
