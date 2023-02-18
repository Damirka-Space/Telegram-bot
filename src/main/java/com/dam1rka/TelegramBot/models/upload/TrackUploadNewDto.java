package com.dam1rka.TelegramBot.models.upload;

import lombok.Data;

@Data
public class TrackUploadNewDto  {
    private String title;
    private String author;

    private byte[] track;

    public String toString() {
        return "" + "Title: " + title + "\n \t" +
                "Author: " + author + "\n \t" +
                "Track: " + title;
    }
}
