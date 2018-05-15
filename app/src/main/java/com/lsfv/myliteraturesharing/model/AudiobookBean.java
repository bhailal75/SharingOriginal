package com.lsfv.myliteraturesharing.model;


import fr.xebia.android.freezer.annotations.Model;

@Model
public class AudiobookBean {
        /**
         * audio_book_id : 6
         * audio_book_description : ANSI C
         */

        public String audio_book_id;
        public String audio_book_description;

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
