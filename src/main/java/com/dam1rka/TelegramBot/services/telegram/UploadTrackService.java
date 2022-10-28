package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.entities.UserEntity;
import com.dam1rka.TelegramBot.models.upload.AlbumUploadDto;
import com.dam1rka.TelegramBot.models.upload.TrackUploadNewDto;
import com.dam1rka.TelegramBot.models.upload.UserUploadAlbum;
import com.dam1rka.TelegramBot.repositories.UserRepository;
import com.dam1rka.TelegramBot.services.TelegramBot;
import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Component
public class UploadTrackService extends TelegramServiceImpl {

    @Value("${files.dir}")
    private String filesDir;
    private final UserRepository userRepository;

    private final HashMap<Long, UserUploadAlbum> users = new HashMap<>();

    public UploadTrackService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void sendMessage(TelegramBot bot, Long chatId, String text, boolean showAnswers) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        if(showAnswers) {
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            message.setReplyMarkup(keyboardMarkup);

            List<KeyboardRow> keyboardRows = new ArrayList<>();
            {
                KeyboardRow keyboardRow = new KeyboardRow();
                KeyboardButton yesAnswer = new KeyboardButton();
                yesAnswer.setText("Yes");
                keyboardRow.add(yesAnswer);

                KeyboardButton noAnswer = new KeyboardButton();
                noAnswer.setText("No");
                keyboardRow.add(noAnswer);
                keyboardRows.add(keyboardRow);
            }

            keyboardMarkup.setKeyboard(keyboardRows);
            keyboardMarkup.setResizeKeyboard(true);
            keyboardMarkup.setOneTimeKeyboard(true);
        }

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private java.io.File downloadDocument(TelegramBot bot, Document document, String localFilePath) throws IOException, TelegramApiException {
        return downloadFile(bot, document.getFileId(), localFilePath);
    }

    private java.io.File downloadAudio(TelegramBot bot, Audio audio, String localFilePath) throws TelegramApiException, IOException {
        return downloadFile(bot, audio.getFileId(), localFilePath);
    }

    private java.io.File downloadFile(TelegramBot bot, String fileId, String localFilePath) throws IOException, TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        File file = bot.execute(getFile);;
        java.io.File localFile = new java.io.File(localFilePath);
        InputStream is = new URL(file.getFileUrl(bot.getBotToken())).openStream();
        FileUtils.copyInputStreamToFile(is, localFile);
        return localFile;
    }

    @Override
    public void handleCommand(Update update, TelegramBot bot) {
        super.handleCommand(update, bot);

        // TODO: make check privileges of user

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
            userUploadAlbum.setState(UserUploadAlbum.State.EnterTitle);
            userUploadAlbum.setUploadDto(new AlbumUploadDto());
            users.put(telegramId, userUploadAlbum);
        } else {
            users.get(telegramId).setState(UserUploadAlbum.State.EnterTitle);
        }

        sendMessage(bot, chatId, EmojiParser.parseToUnicode("You started service of album uploading... :guitar:"), false);

        sendMessage(bot, chatId, "First of all, enter title of album", false);
    }

    @Override
    public boolean handleMessage(Update update, TelegramBot bot) {
        Long telegramId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        if(users.containsKey(telegramId)) {
            UserUploadAlbum user = users.get(telegramId);
            AlbumUploadDto uploadDto = user.getUploadDto();
            switch (user.getState()) {
                case EnterTitle -> {
                    uploadDto.setTitle(update.getMessage().getText());
                    sendMessage(bot, chatId, "Next step is authors.. enter authors", false);
                    user.setState(UserUploadAlbum.State.EnterAuthors);
                }
                case EnterAuthors -> {
                    uploadDto.setAuthor(update.getMessage().getText());
                    sendMessage(bot, chatId, "Don't forget genre - enter album genre", false);
                    user.setState(UserUploadAlbum.State.EnterGenre);
                }
                case EnterGenre -> {
                    uploadDto.setGenre(update.getMessage().getText());
                    sendMessage(bot, chatId, "The next one is art! Send me your image (only as document!)", false);
                    user.setState(UserUploadAlbum.State.UploadArt);
                }
                case UploadTrackEnterTitle -> {
                    List<TrackUploadNewDto> tracks = uploadDto.getTracks();
                    TrackUploadNewDto track = new TrackUploadNewDto();
                    tracks.add(track);

                    track.setTitle(update.getMessage().getText());

                    sendMessage(bot, chatId, "Next step is authors", false);
                    user.setState(UserUploadAlbum.State.UploadTrackEnterAuthor);
                }
                case UploadTrackEnterAuthor -> {
                    TrackUploadNewDto track = uploadDto.getTracks().get(uploadDto.getTracks().size() - 1);
                    track.setAuthor(update.getMessage().getText());
                    sendMessage(bot, chatId, EmojiParser.parseToUnicode("Last is file, send me track! :musical_note:"), false);
                    user.setState(UserUploadAlbum.State.UploadTrack);
                }
                case UploadTrackEnd -> {
                    if(update.getMessage().getText().equals("Yes")) {
                        sendMessage(bot, chatId, "Okay, let's begin!", false);
                        sendMessage(bot, chatId, "First this is title, give me title!", false);
                        user.setState(UserUploadAlbum.State.UploadTrackEnterTitle);
                    } else {
                        sendMessage(bot, chatId, "I think it's no", false);
                        sendMessage(bot, chatId, "So what we got", false);
                        sendMessage(bot, chatId, uploadDto.toString(), false);
                        sendMessage(bot, chatId, "Send album to server? (Yes/No)", true);
                        user.setState(UserUploadAlbum.State.End);
                    }
                }
                case End -> {
                    if(update.getMessage().getText().equals("Yes")) {
                        sendMessage(bot, chatId, "Got it, send to server...", false);
                        sendMessage(bot, chatId, EmojiParser.parseToUnicode("spooky sent :ghost:"), false);
                    } else {
                        sendMessage(bot, chatId, "Okay, I don't like it too)", false);
                    }
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
                case EnterTitle -> sendMessage(bot, chatId, "I want title!", false);
                case EnterAuthors -> sendMessage(bot, chatId, "Gimme a text!", false);
                case EnterGenre -> sendMessage(bot, chatId, "It's not a text", false);
                case UploadArt -> {
                    if(!update.getMessage().hasDocument()) {
                        sendMessage(bot, chatId, "Please, send photo as document!", false);
                        return true;
                    }
                    Document img = update.getMessage().getDocument();
                    try {
                        java.io.File file = downloadDocument(bot, img, filesDir + img.getFileUniqueId() + ".jpg");
                        FileInputStream input = new FileInputStream(file);
                        MultipartFile image = new MockMultipartFile(uploadDto.getTitle(),
                                file.getName(), "image/jpeg", IOUtils.toByteArray(input));
                        uploadDto.setImage(image);

                        user.setState(UserUploadAlbum.State.UploadTrackEnterTitle);
                        sendMessage(bot, chatId, "The last one is tracks.. Send me tracks!", false);
                        sendMessage(bot, chatId, "First this is title, give me title!", false);
                    } catch (IOException | TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                }
                case UploadTrack -> {
                    if(!update.getMessage().hasAudio()) {
                        sendMessage(bot, chatId, "Please, send track", false);
                        return true;
                    }
                    Audio tr = update.getMessage().getAudio();
                    TrackUploadNewDto track = uploadDto.getTracks().get(uploadDto.getTracks().size() - 1);
                    try {
                        java.io.File file = downloadAudio(bot, tr, filesDir + tr.getFileUniqueId() + ".mp3");
                        FileInputStream input = new FileInputStream(file);
                        MultipartFile trackFile = new MockMultipartFile(track.getTitle(),
                                file.getName(), "audio/mpeg", IOUtils.toByteArray(input));
                        track.setTrack(trackFile);

                        user.setState(UserUploadAlbum.State.UploadTrackEnd);
                        sendMessage(bot, chatId, "Do you want to upload another? (Yes/No)", true);
                    } catch (IOException | TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return true;
        }

        return false;
    }
}
