package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.models.BotServices;
import com.dam1rka.TelegramBot.services.TelegramBot;
import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HelpService extends TelegramServiceImpl {

    @Override
    public void handleCommand(Update update, TelegramBot bot) {
        super.handleCommand(update, bot);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(update.getMessage().getChatId()));
        message.setText(BotServices.getHelpText());

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
