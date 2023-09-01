package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.entities.UserEntity;
import com.dam1rka.TelegramBot.models.upload.AlbumUploadDto;
import com.dam1rka.TelegramBot.models.upload.TrackUploadNewDto;
import com.dam1rka.TelegramBot.models.upload.UserUploadAlbum;
import com.dam1rka.TelegramBot.repositories.UserRepository;
import com.dam1rka.TelegramBot.services.TelegramBot;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@Component
public class FastUploadService extends UploadTrackService {

    public FastUploadService(WebClient webClient, UserRepository userRepository) {
        super(webClient, userRepository);
    }

    @Override
    public void handleCommand(Update update, TelegramBot bot) {
        Long telegramId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        UserEntity user = userRepository.findByTelegramId(telegramId);
        if(Objects.isNull(user)) {
            sendMessage(bot, chatId, "You can't get access to this feature, because you don't registered your profile!", false);
            return;
        }

        if(!users.containsKey(telegramId)) {
            UserUploadAlbum userUploadAlbum = new UserUploadAlbum();
            userUploadAlbum.setUser(user);
            userUploadAlbum.setState(UserUploadAlbum.State.FastUploadStart);
            userUploadAlbum.setUploadDto(new AlbumUploadDto());
            users.put(telegramId, userUploadAlbum);
        } else {
            users.get(telegramId).setState(UserUploadAlbum.State.FastUploadStart);
        }

        sendMessage(bot, chatId, "You're in, please send me all track from one album (with metadata!) and after send !upload message to upload to the server", false);
    }

    @Override
    public boolean handleMessage(Update update, TelegramBot bot) {
        Long telegramId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        Message msg = update.getMessage();
        if(users.containsKey(telegramId)) {
            UserUploadAlbum user = users.get(telegramId);

            if(Objects.equals(msg.getText(), "!upload")) {
                user.setState(UserUploadAlbum.State.FastUploadEnd);

                sendToServer(bot, chatId, user);

                user.setState(UserUploadAlbum.State.Start);

                return true;
            }
        }
        return false;
    }

    private void setAlbum(AlbumUploadDto album, ID3v2 metadata) {
        if(Objects.isNull(album.getTitle())) {
            album.setTitle(metadata.getAlbum());
            album.setAuthor(metadata.getAlbumArtist());
            album.setGenre(metadata.getGenreDescription());
        }
    }

    @Override
    public boolean handleOther(Update update, TelegramBot bot) {
        Long telegramId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        if(users.containsKey(telegramId)) {
            UserUploadAlbum user = users.get(telegramId);
            AlbumUploadDto uploadDto = user.getUploadDto();

            if(user.getState() == UserUploadAlbum.State.FastUploadStart) {
                if(update.getMessage().hasDocument()) {
                    Document img = update.getMessage().getDocument();
                    try {
                        java.io.File file = downloadDocument(bot, img, filesDir + img.getFileUniqueId() + ".jpg");
                        FileInputStream input = new FileInputStream(file);
                        MultipartFile image = new MockMultipartFile(uploadDto.getTitle(),
                                file.getName(), "image/jpeg", IOUtils.toByteArray(input));
                        uploadDto.setImage(image.getBytes());
                    } catch (IOException | TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }

                else if(update.getMessage().hasAudio()) {
                    Audio tr = update.getMessage().getAudio();
                    TrackUploadNewDto track = new TrackUploadNewDto();
                    try {
                        java.io.File file = downloadAudio(bot, tr, filesDir + tr.getFileUniqueId() + ".mp3");

                        Mp3File tags = new Mp3File(file);

                        if(tags.hasId3v2Tag()) {
                            var metadata = tags.getId3v2Tag();

                            setAlbum(uploadDto, metadata);

                            track.setTitle(metadata.getTitle());
                            track.setAuthor(metadata.getArtist());
                            track.setDuration(tr.getDuration());

                            FileInputStream input = new FileInputStream(file);

                            MultipartFile trackFile = new MockMultipartFile(track.getTitle(),
                                    file.getName(), "audio/mpeg", IOUtils.toByteArray(input));

                            track.setTrack(trackFile.getBytes());

                            uploadDto.getTracks().add(track);
                        }

                    } catch (IOException | TelegramApiException | InvalidDataException | UnsupportedTagException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    return false;
                }

                return true;
            }

        }
        return false;
    }
}
