package com.dam1rka.TelegramBot.services.interfaces;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramServiceImpl implements ITelegramService {

    Update lastUpdate;

    @Override
    public void handleCommand(Update update) {
        lastUpdate = update;
    }

    @Override
    public BotApiMethod getResult() {
        return new SendMessage(String.valueOf(lastUpdate.getMessage().getChatId()), "Not implemented service");
    }

    @Override
    public boolean handleMessage(Update update) {
        return false;
    }
}
