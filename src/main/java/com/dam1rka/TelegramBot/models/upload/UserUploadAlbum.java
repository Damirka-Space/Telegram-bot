package com.dam1rka.TelegramBot.models.upload;

import com.dam1rka.TelegramBot.entities.UserEntity;
import lombok.Data;

@Data
public class UserUploadAlbum {
    public enum State {
        Start,
        EnterTitle,
        EnterAuthors,
        EnterGenre,
        UploadArt,

        UploadTrackStart,
        UploadTrackEnterTitle,
        UploadTrackEnterAuthor,
        UploadTrack,
        UploadTrackEnd,
        
        End,
    }

    private UserEntity user;
    private State state;
    private AlbumUploadDto uploadDto;

}
