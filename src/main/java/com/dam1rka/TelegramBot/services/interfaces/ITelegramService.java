package com.dam1rka.TelegramBot.services.interfaces;

import com.dam1rka.TelegramBot.models.ServiceStates;
import com.dam1rka.TelegramBot.services.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.net.MalformedURLException;


public interface ITelegramService {

    ServiceStates state = ServiceStates.none;

    // handle messages with /
    void handleCommand(Update update, TelegramBot bot);

    // handle all others messages
    boolean handleMessage(Update update, TelegramBot bot);

    boolean handleOther(Update update, TelegramBot bot);


}
