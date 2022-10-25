package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnknownCommandService extends TelegramServiceImpl {

    SendMessage message = new SendMessage();
    @Override
    public void handleCommand(Update update) {
        message.setChatId(String.valueOf(update.getMessage().getChatId()));
        message.setText("Sorry, command was not recognized");
    }

    @Override
    public BotApiMethod getResult() {
        return message;
    }
}