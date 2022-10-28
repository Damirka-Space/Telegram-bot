package com.dam1rka.TelegramBot.models.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AlbumUploadDto {
    private String title;
    private String author;
    private String genre;
    private MultipartFile image;
    private List<TrackUploadNewDto> tracks;
}
