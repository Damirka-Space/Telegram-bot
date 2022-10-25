package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TestService extends TelegramServiceImpl {

    SendMessage message = new SendMessage();

    @Override
    public void handleCommand(Update update) {
        super.handleCommand(update);
        message.setChatId(update.getMessage().getChatId());
        message.setText("Your id is " + update.getMessage().getFrom().getId());
    }

    @Override
    public BotApiMethod getResult() {
        return message;
    }
}
