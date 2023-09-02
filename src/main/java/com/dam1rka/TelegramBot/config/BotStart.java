package com.dam1rka.TelegramBot.config;

import com.dam1rka.TelegramBot.services.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class BotStart {
    private final TelegramBot telegramBot;
//    private final TelegramAbilityBot telegramAbilityBot;

    public BotStart(TelegramBot telegramBot/*, TelegramAbilityBot telegramAbilityBot */) {
        this.telegramBot = telegramBot;
//        this.telegramAbilityBot = telegramAbilityBot;
    }

    @PostConstruct
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramBot);
//            telegramBotsApi.registerBot(telegramAbilityBot);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
