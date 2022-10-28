package com.dam1rka.TelegramBot.models.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TrackUploadNewDto {
    private String title;
    private String author;
    private MultipartFile track;

    public String toString() {
        return "" + "Title: " + title + "\n \t" +
                "Author: " + author + "\n \t" +
                "Track: " + track.getName();
    }
}
