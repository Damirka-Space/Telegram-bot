package com.dam1rka.TelegramBot.services.interfaces;

import com.dam1rka.TelegramBot.models.ServiceStates;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


public interface ITelegramService {

    ServiceStates state = ServiceStates.none;

    // handle messages with /
    void handleCommand(Update update);

    // returns result of command
    BotApiMethod getResult();

    // handle all others messages
    boolean handleMessage(Update update);

    boolean handleOther(Update update);


}
