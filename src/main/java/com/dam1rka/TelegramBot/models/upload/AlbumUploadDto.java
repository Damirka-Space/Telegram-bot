package com.dam1rka.TelegramBot.models.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@Data
public class AlbumUploadDto {
    private String title;
    private String author;
    private String genre;
    private MultipartFile image;
    private List<TrackUploadNewDto> tracks = new LinkedList<>();

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("Title: ").append(title).append("\n");
        stringBuilder.append("Author: ").append(author).append("\n");
        stringBuilder.append("Image: ").append(image.getName()).append("\n");
        stringBuilder.append("Tracks:\n");
        for (int i = 0; i < tracks.size(); i++)
            stringBuilder.append("\t").append(i + 1).append(") ").append(tracks.get(i).toString()).append("\n" +
                    "");
        return stringBuilder.toString();
    }
}
