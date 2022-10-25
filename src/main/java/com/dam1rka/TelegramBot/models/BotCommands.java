package com.dam1rka.TelegramBot.models;

import com.dam1rka.TelegramBot.services.interfaces.ITelegramService;
import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import com.dam1rka.TelegramBot.services.telegram.HelpService;
import com.dam1rka.TelegramBot.services.telegram.RegistrationService;
import com.dam1rka.TelegramBot.services.telegram.StartService;
import com.dam1rka.TelegramBot.services.telegram.TestService;

public enum BotCommands {
    start,
    help,
    register,
    myData,
    test;

    @Override
    public String toString() {
        switch (ordinal()) {
            case 0 -> {
                return "/start";
            }
            case 1 -> {
                return "/help";
            }
            case 2 -> {
                return "/register";
            }
            case 3 -> {
                return "/mydata";
            }
            case 4 -> {
                return "/test";
            }
            default -> {
                return "";
            }
        }
    }

    public String getDescription() {
        switch (ordinal()) {
            case 0 -> {
                return "to see a welcome message";
            }
            case 1 -> {
                return "to see this message again";
            }
            case 2 -> {
                return "to register your profile";
            }
            case 3 -> {
                return "to see data stored about yourself";
            }
            case 4 -> {
                return "just test";
            }
            default -> {
                return "";
            }
        }
    }

    public ITelegramService getService() {
        switch (ordinal()) {
            case 0 -> {
                return new StartService();
            }
            case 1 -> {
                return new HelpService();
            }
            case 2 -> {
                return new RegistrationService();
            }
            case 4 -> {
                return new TestService();
            }
            default -> {
                return new TelegramServiceImpl();
            }
        }
    }

    public static String getHelpText() {
        StringBuilder str = new StringBuilder();
        str.append("""
                This bot is created to demonstrate Spring capabilities.
                You can execute commands from the main menu on the left or by typing a command:
                """);


        for (int i = 0; i < BotCommands.values().length; i++) {
            str.append("Type ")
                    .append(BotCommands.values()[i].toString())
                    .append(" ")
                    .append(BotCommands.values()[i].getDescription())
                    .append("\n");
        }

        return str.toString();
    }
}