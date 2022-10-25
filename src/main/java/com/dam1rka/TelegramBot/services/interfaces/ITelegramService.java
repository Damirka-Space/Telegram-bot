package com.dam1rka.TelegramBot.services.interfaces;

import com.dam1rka.TelegramBot.models.ServiceStates;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


public interface ITelegramService {

    ServiceStates state = ServiceStates.none;


    void handle(Update update);

    BotApiMethod getResult();


}
