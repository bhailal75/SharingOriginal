package com.lsfv.literaturesharing.model;

import java.util.List;



public class BookListModel {

    /**
     * status : 1
     * Audiobook : [{"audio_book_id":"6","audio_book_description":"ANSI C"}]
     * Message : Audiobook fetched successfully
     */

    private String status;
    private String Message;
    private List<AudiobookBean> Audiobook;

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

    public List<AudiobookBean> getAudiobook() {
        return Audiobook;
    }

    public void setAudiobook(List<AudiobookBean> Audiobook) {
        this.Audiobook = Audiobook;
    }

    public static class AudiobookBean {
        /**
         * audio_book_id : 6
         * audio_book_description : ANSI C
         */

        private String audio_book_id;
        private String audio_book_description;

        public String getAudio_book_id() {
            return audio_book_id;
        }

        public void setAudio_book_id(String audio_book_id) {
            this.audio_book_id = audio_book_id;
        }

        public String getAudio_book_description() {
            return audio_book_description;
        }

        public void setAudio_book_description(String audio_book_description) {
            this.audio_book_description = audio_book_description;
        }
    }
}
