package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartService extends TelegramServiceImpl {
    SendMessage message = new SendMessage();;

    @Override
    public void handle(Update update) {
        super.handle(update);

        String name = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :blush:");

        message.setChatId(String.valueOf(chatId));
        message.setText(answer);
    }

    @Override
    public BotApiMethod getResult() {
        return message;
    }
}
