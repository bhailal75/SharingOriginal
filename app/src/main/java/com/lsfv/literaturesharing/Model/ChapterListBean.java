package com.lsfv.literaturesharing.model;

import fr.xebia.android.freezer.annotations.Model;


@Model
public class ChapterListBean  {
        /**
         * chapter_id : 1
         * chapter_file : http://www.lsfv.in/admin/chapters/001.mp3
         * duration : 00:15:00
         * chapter_desc : Prakaran 1
         */


        public int chapter_id;
        public String chapter_file;
        public String duration;
        public String chapter_desc;

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