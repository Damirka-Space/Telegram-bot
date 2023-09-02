package com.dam1rka.TelegramBot.models.upload;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class AlbumUploadDto {
    private String title;
    private String author;
    private String genre;
    private byte[] image;
    private List<TrackUploadNewDto> tracks = new LinkedList<>();

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("Title: ").append(title).append("\n");
        stringBuilder.append("Author: ").append(author).append("\n");
        stringBuilder.append("Genre: ").append(genre).append("\n");
        stringBuilder.append("Image: ").append(title).append("\n");
        stringBuilder.append("Tracks:\n");
        for (int i = 0; i < tracks.size(); i++)
            stringBuilder.append("\t").append(i + 1).append(") ").append(tracks.get(i).toString()).append("\n" +
                    "");
        return stringBuilder.toString();
    }
}
