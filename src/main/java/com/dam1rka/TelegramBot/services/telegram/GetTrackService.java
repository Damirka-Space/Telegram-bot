package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.services.TelegramBot;
import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GetTrackService extends TelegramServiceImpl {

    @Override
    public void handleCommand(Update update, TelegramBot bot) {
        super.handleCommand(update, bot);
        SendMessage message = new SendMessage();
        message.setText("Enter number of track");
        message.setChatId(update.getMessage().getChatId());

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean handleMessage(Update update, TelegramBot bot){
        if(StringUtils.isNumeric(update.getMessage().getText())) {
            SendAudio audio = new SendAudio();
            audio.setTitle("Test");
            audio.setAudio(new InputFile("http://130.61.79.90:8090/api/track/get/" + update.getMessage().getText()));
            audio.setChatId(update.getMessage().getChatId());

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
            return  true;
        }

        return false;
    }

}
