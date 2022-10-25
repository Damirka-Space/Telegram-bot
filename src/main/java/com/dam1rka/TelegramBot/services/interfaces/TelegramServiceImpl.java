package com.dam1rka.TelegramBot.services.interfaces;

import com.dam1rka.TelegramBot.services.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.net.MalformedURLException;

public class TelegramServiceImpl implements ITelegramService {

    Update lastUpdate;

    @Override
    public void handleCommand(Update update, TelegramBot bot) {
        lastUpdate = update;
    }

    @Override
    public boolean handleMessage(Update update, TelegramBot bot) {
        return false;
    }

    @Override
    public boolean handleOther(Update update, TelegramBot bot) {
        return false;
    }
}
