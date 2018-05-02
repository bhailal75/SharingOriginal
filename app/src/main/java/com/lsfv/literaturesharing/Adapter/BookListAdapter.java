package com.lsfv.literaturesharing.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsfv.literaturesharing.model.BookListModel;
import com.lsfv.literaturesharing.R;
import java.util.ArrayList;
import java.util.List;


public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder>{

    private Context context;
    private List<BookListModel.AudiobookBean> audioBookList;
    private BookClickListner bookClickListner;


    public BookListAdapter(Context context, ArrayList<BookListModel.AudiobookBean> audiobookList, BookClickListner bookclicklistner) {
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
        BookListModel.AudiobookBean bookModel = audioBookList.get(position);

        holder.textView.setText(bookModel.getAudio_book_description().toString());
        holder.rvBookRaw.setTag(position);
    }

    @Override
    public int getItemCount() {
        return audioBookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView textView;
        RelativeLayout rvBookRaw;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.right_arrow);
            textView=(TextView)itemView.findViewById(R.id.book_name);
            rvBookRaw = (RelativeLayout) itemView.findViewById(R.id.book_Raw);
            rvBookRaw.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (bookClickListner != null){
                int pos = (int) view.getTag();
                bookClickListner.onBookClick(pos);
            }
        }
    }

    public interface BookClickListner {
        void onBookClick(int pos);
    }
}

