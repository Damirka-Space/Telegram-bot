package com.dam1rka.TelegramBot.services.telegram;

import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class RegistrationService extends TelegramServiceImpl {

    SendMessage message = new SendMessage();

    @Override
    public void handleCommand(Update update) {
        super.handleCommand(update);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(keyboardMarkup);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("Test");

        keyboardRow.add("Test2");

        keyboardRows.add(keyboardRow);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        message.setText("test");
        message.setChatId(update.getMessage().getChatId());
    }

    @Override
    public BotApiMethod getResult() {
        return message;
    }
}
