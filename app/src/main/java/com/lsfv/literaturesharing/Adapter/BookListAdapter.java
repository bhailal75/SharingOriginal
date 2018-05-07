package com.lsfv.literaturesharing.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsfv.literaturesharing.model.AudiobookBean;
import com.lsfv.literaturesharing.model.AudiobookBeanEntityManager;
import com.lsfv.literaturesharing.model.BookListModel;
import com.lsfv.literaturesharing.R;
import com.lsfv.literaturesharing.model.ChapterListBean;
import com.lsfv.literaturesharing.model.ChapterListBeanEntityManager;

import java.util.ArrayList;
import java.util.List;


public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder>{

    private ArrayList<AudiobookBean> downloadedBook;
    private Context context;
    private List<AudiobookBean> audioBookList;
    private BookClickListner bookClickListner;


    public BookListAdapter(Context context, ArrayList<AudiobookBean> audiobookList, BookClickListner bookclicklistner) {
        this.context = context;
        this.audioBookList = audiobookList;
        this.bookClickListner = bookclicklistner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklist_cus_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AudiobookBean bookModel = audioBookList.get(position);

        for (int i = 0; i < downloadedBook.size(); i++) {
            if (downloadedBook != null && downloadedBook.size() > 0 && downloadedBook.get(i).getAudio_book_id().equals(audioBookList.get(position).getAudio_book_id())){
                holder.downloadBookBtn.setVisibility(View.GONE);
            }
        }
        holder.textView.setText(bookModel.getAudio_book_description().toString());
        holder.rvBookRaw.setTag(position);
        holder.downloadBookBtn.setTag(position);
    }

    @Override
    public int getItemCount() {
        return audioBookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Button downloadBookBtn;
        TextView textView;
        RelativeLayout rvBookRaw;
        public ViewHolder(View itemView) {
            super(itemView);
            downloadBookBtn=(Button) itemView.findViewById(R.id.download_book_btn);
            textView=(TextView)itemView.findViewById(R.id.book_name);
            rvBookRaw = (RelativeLayout) itemView.findViewById(R.id.book_Raw);

            downloadedBook = new ArrayList<>();
            AudiobookBeanEntityManager audiobookBeanEntityManager = new AudiobookBeanEntityManager();
            if (audiobookBeanEntityManager.count() > 0){
                downloadedBook = (ArrayList<AudiobookBean>) audiobookBeanEntityManager.select().asList();
            }
            rvBookRaw.setOnClickListener(this);
            downloadBookBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == downloadBookBtn){
                if (bookClickListner != null){
                    int pos = (int) view.getTag();
                    AudiobookBeanEntityManager audiobookBeanEntityManager = new AudiobookBeanEntityManager();
                    AudiobookBean audiobookBean = new AudiobookBean();
                    audiobookBean.setAudio_book_id(audioBookList.get(pos).getAudio_book_id());
                    audiobookBean.setAudio_book_description(audioBookList.get(pos).getAudio_book_description());
                    audiobookBeanEntityManager.add(audiobookBean);
                    downloadBookBtn.setVisibility(View.GONE);
                    bookClickListner.onDownloadBookClick(pos);
                }
            }else if(bookClickListner != null){
                int pos = (int) view.getTag();
                bookClickListner.onBookClick(pos);
            }
        }
    }

    public interface BookClickListner {
        void onBookClick(int pos);
        void onDownloadBookClick(int pos);
    }
}

