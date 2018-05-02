package com.lsfv.literaturesharing.model;

import java.util.List;



public class ChapterListModel {


    /**
     * status : 1
     * Message : chapter fetched successfully
     * ChapterList : [{"chapter_id":1,"chapter_file":"http://www.lsfv.in/admin/chapters/001.mp3","duration":"00:15:00","chapter_desc":"Prakaran 1"},{"chapter_id":2,"chapter_file":"http://www.lsfv.in/admin/chapters/002.mp3","duration":"00:13:22","chapter_desc":"Prakaran 2"},{"chapter_id":3,"chapter_file":"http://www.lsfv.in/admin/chapters/003.mp3","duration":"00:14:34","chapter_desc":"Prakaran 3"},{"chapter_id":4,"chapter_file":"http://www.lsfv.in/admin/chapters/004.mp3","duration":"00:11:52","chapter_desc":"Prakaran 4"},{"chapter_id":5,"chapter_file":"http://www.lsfv.in/admin/chapters/005.mp3","duration":"00:17:09","chapter_desc":"prakran 5"},{"chapter_id":6,"chapter_file":"http://www.lsfv.in/admin/chapters/006.mp3","duration":"00:14:39","chapter_desc":"Prakaran 6"}]
     */

    private String status;
    private String Message;
    private List<ChapterListBean> ChapterList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public List<ChapterListBean> getChapterList() {
        return ChapterList;
    }

    public void setChapterList(List<ChapterListBean> ChapterList) {
        this.ChapterList = ChapterList;
    }


}
