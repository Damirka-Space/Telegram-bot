package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.models.BotCommands;
import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpService extends TelegramServiceImpl {
    SendMessage message = new SendMessage();
    @Override
    public void handle(Update update) {
        super.handle(update);

        message.setChatId(String.valueOf(update.getMessage().getChatId()));
        message.setText(BotCommands.getHelpText());
    }

    @Override
    public BotApiMethod getResult() {
        return message;
    }
}
