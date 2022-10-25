package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.services.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistrationService {

    private final TelegramBot telegramBot;

    @Autowired
    public RegistrationService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }




}
