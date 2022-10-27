package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.services.TelegramBot;
import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Component
public class GetTrackService extends TelegramServiceImpl {

    @Value("${track.dir}")
    private String trackDir;

    @Override
    public void handleCommand(Update update, TelegramBot bot) {
        super.handleCommand(update, bot);
        SendMessage message = new SendMessage();
        message.setText("Enter id of track or send url of track");
        message.setChatId(update.getMessage().getChatId());

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean handleMessage(Update update, TelegramBot bot){
        SendAudio audio = new SendAudio();
        audio.setChatId(update.getMessage().getChatId());

        if(StringUtils.isNumeric(update.getMessage().getText())) {
            String title = "Track-" + update.getMessage().getText() + ".mp3";
//            audio.setTitle(title);
            audio.setAudio(new InputFile(
                    new File(trackDir + title)));

            sendAudio(update, bot, audio);
            return true;
        } else if (update.getMessage().getText().startsWith("http")) {
            audio.setAudio(new InputFile(update.getMessage().getText()));
            audio.setChatId(update.getMessage().getChatId());

            sendAudio(update, bot, audio);
            return true;
        }

        return false;
    }

    private void sendAudio(Update update, TelegramBot bot, SendAudio audio) {
        try {
            bot.execute(audio);
        } catch (TelegramApiException e) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText("Sorry, can't upload track");
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
