package com.lsfv.literaturesharing.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChapterListBean implements Parcelable {
        /**
         * chapter_id : 1
         * chapter_file : http://www.lsfv.in/admin/chapters/001.mp3
         * duration : 00:15:00
         * chapter_desc : Prakaran 1
         */


        protected int chapter_id;
        private String chapter_file;
        private String duration;
        private String chapter_desc;

        public int getChapter_id() {
            return chapter_id;
        }

        public void setChapter_id(int chapter_id) {
            this.chapter_id = chapter_id;
        }

        public String getChapter_file() {
            return chapter_file;
        }

        public void setChapter_file(String chapter_file) {
            this.chapter_file = chapter_file;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getChapter_desc() {
            return chapter_desc;
        }

        public void setChapter_desc(String chapter_desc) {
            this.chapter_desc = chapter_desc;
        }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.chapter_id);
        dest.writeString(this.chapter_file);
        dest.writeString(this.duration);
        dest.writeString(this.chapter_desc);
    }

    public ChapterListBean() {
    }

    protected ChapterListBean(Parcel in) {
        this.chapter_id = in.readInt();
        this.chapter_file = in.readString();
        this.duration = in.readString();
        this.chapter_desc = in.readString();
    }

    public static final Parcelable.Creator<ChapterListBean> CREATOR = new Parcelable.Creator<ChapterListBean>() {
        @Override
        public ChapterListBean createFromParcel(Parcel source) {
            return new ChapterListBean(source);
        }

        @Override
        public ChapterListBean[] newArray(int size) {
            return new ChapterListBean[size];
        }
    };
}


///////////////////////////
//public class ChapterListBean {
//    /**
//     * chapter_id : 1
//     * chapter_file : http://www.lsfv.in/admin/chapters/001.mp3
//     * duration : 00:15:00
//     * chapter_desc : Prakaran 1
//     */
//
//    private int chapter_id;
//    private String chapter_file;
//    private String duration;
//    private String chapter_desc;
//
//    public int getChapter_id() {
//        return chapter_id;
//    }
//
//    public void setChapter_id(int chapter_id) {
//        this.chapter_id = chapter_id;
//    }
//
//    public String getChapter_file() {
//        return chapter_file;
//    }
//
//    public void setChapter_file(String chapter_file) {
//        this.chapter_file = chapter_file;
//    }
//
//    public String getDuration() {
//        return duration;
//    }
//
//    public void setDuration(String duration) {
//        this.duration = duration;
//    }
//
//    public String getChapter_desc() {
//        return chapter_desc;
//    }
//
//    public void setChapter_desc(String chapter_desc) {
//        this.chapter_desc = chapter_desc;
//    }
//
//
//}