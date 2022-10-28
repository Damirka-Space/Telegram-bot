package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.entities.UserEntity;
import com.dam1rka.TelegramBot.models.upload.AlbumUploadDto;
import com.dam1rka.TelegramBot.models.upload.UserUploadAlbum;
import com.dam1rka.TelegramBot.repositories.UserRepository;
import com.dam1rka.TelegramBot.services.TelegramBot;
import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;


@Component
public class UploadTrackService extends TelegramServiceImpl {

    @Value("${files.dir}")
    private String filesDir;
    @Autowired
    private UserRepository userRepository;

    private HashMap<Long, UserUploadAlbum> users = new HashMap<>();

    private void sendMessage(TelegramBot bot, Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private java.io.File downloadFile(TelegramBot bot, Document document, String localFilePath) throws IOException, TelegramApiException {
        File file = getFilePath(bot, document);

        java.io.File localFile = new java.io.File(localFilePath);
        InputStream is = new URL(file.getFileUrl(bot.getBotToken())).openStream();
        FileUtils.copyInputStreamToFile(is, localFile);
        return  localFile;
    }

    private File getFilePath(TelegramBot bot, Document document) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        return bot.execute(getFile);
    }

    @Override
    public void handleCommand(Update update, TelegramBot bot) {
        super.handleCommand(update, bot);

        // TODO: make check privileges of user

        Long telegramId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        if(!users.containsKey(telegramId)) {
            UserEntity user = userRepository.findByTelegramId(telegramId);
            if(Objects.isNull(user)) {
                sendMessage(bot, chatId, "You can't get access to this feature, because you don't registered your profile!");
                return;
            }
            sendMessage(bot, chatId, "You started service of album uploading...");

            UserUploadAlbum userUploadAlbum = new UserUploadAlbum();
            userUploadAlbum.setUser(user);
            userUploadAlbum.setState(UserUploadAlbum.State.EnterTitle);
            userUploadAlbum.setUploadDto(new AlbumUploadDto());
            users.put(telegramId, userUploadAlbum);

            sendMessage(bot, chatId, "First of all, enter title of album");
        }
    }



    @Override
    public boolean handleMessage(Update update, TelegramBot bot) {
        Long telegramId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        if(users.containsKey(telegramId)) {
            UserUploadAlbum user = users.get(telegramId);
            AlbumUploadDto uploadDto = user.getUploadDto();
            switch (user.getState()) {
                case Start -> {
                    // Skip for now
                }
                case EnterTitle -> {
                    uploadDto.setTitle(update.getMessage().getText());
                    sendMessage(bot, chatId, "Next step is authors.. enter authors");
                    user.setState(UserUploadAlbum.State.EnterAuthors);
                }
                case EnterAuthors -> {
                    uploadDto.setAuthor(update.getMessage().getText());
                    sendMessage(bot, chatId, "Don't forget genre - enter album genre");
                    user.setState(UserUploadAlbum.State.EnterGenre);
                }
                case EnterGenre -> {
                    uploadDto.setGenre(update.getMessage().getText());
                    sendMessage(bot, chatId, "The next one is art! Send me your image (only as document!)");
                    user.setState(UserUploadAlbum.State.UploadArt);
                }
                case UploadArt -> {
                }
                case UploadTrackStart -> {
                }
                case UploadTrackEnterTitle -> {
                }
                case UploadTrackEnterAuthor -> {
                }
                case UploadTrack -> {
                }
                case UploadTrackEnd -> {
                }
                case End -> {
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleOther(Update update, TelegramBot bot) {
        Long telegramId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        if(users.containsKey(telegramId)) {
            UserUploadAlbum user = users.get(telegramId);
            AlbumUploadDto uploadDto = user.getUploadDto();
            switch (user.getState()) {
                case Start -> {
                }
                case EnterTitle -> {
                    sendMessage(bot, chatId, "I want title!");
                }
                case EnterAuthors -> {
                    sendMessage(bot, chatId, "Gimme a text!");
                }
                case EnterGenre -> {
                    sendMessage(bot, chatId, "It's not a text");
                }
                case UploadArt -> {
                    if(!update.getMessage().hasDocument()) {
                        sendMessage(bot, chatId, "Please, send photo as document!");
                        return true;
                    }
                    Document img = update.getMessage().getDocument();
                    try {
                        java.io.File file = downloadFile(bot, img, filesDir + img.getFileUniqueId() + ".jpg");
                        FileInputStream input = new FileInputStream(file);
                        MultipartFile image = new MockMultipartFile(uploadDto.getTitle(),
                                file.getName(), "image/jpeg", IOUtils.toByteArray(input));
                        uploadDto.setImage(image);

                    } catch (IOException | TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                }
                case UploadTrackStart -> {
                }
                case UploadTrackEnterTitle -> {
                }
                case UploadTrackEnterAuthor -> {
                }
                case UploadTrack -> {
                }
                case UploadTrackEnd -> {
                }
                case End -> {
                }
            }
            return true;
        }

        return false;
    }
}
