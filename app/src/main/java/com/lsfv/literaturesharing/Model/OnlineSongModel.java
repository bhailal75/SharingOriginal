package com.lsfv.literaturesharing.model;



public class OnlineSongModel {

    String chapter_desc;
    String chapter_file;
    String duration;


    public OnlineSongModel(String chapter_desc, String chapter_file, String duration) {
        this.chapter_desc=chapter_desc;
        this.chapter_file=chapter_file;
        this.duration=duration;
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

    public String getChapter_file() {
        return chapter_file;
    }

    public void setChapter_file(String chapter_file) {
        this.chapter_file = chapter_file;
    }
}
