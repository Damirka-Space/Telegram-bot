package com.dam1rka.TelegramBot.models;

import com.dam1rka.TelegramBot.services.interfaces.ITelegramService;
import com.dam1rka.TelegramBot.services.interfaces.TelegramServiceImpl;
import com.dam1rka.TelegramBot.services.telegram.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BotServices {
    public enum Commands {
        start,
        help,
        register,
        myData,
        test,
        getTrack,
        uploadAlbum;
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
                case 5 -> {
                    return "/gettrack";
                }
                case 6 -> {
                    return "/uploadalbum";
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
                case 5 -> {
                    return "get track from server";
                }
                case 6 -> {
                    return "upload album to server";
                }
                default -> {
                    return "";
                }
            }
        }
    }

    private final RegistrationService registrationService;
    private final GetTrackService getTrackService;
    private final UploadTrackService uploadTrackService;

    public BotServices(RegistrationService registrationService, GetTrackService getTrackService, UploadTrackService uploadTrackService) {
        this.registrationService = registrationService;
        this.getTrackService = getTrackService;
        this.uploadTrackService = uploadTrackService;
    }

    public ITelegramService getService(Commands command) {
        switch (command) {
            case start -> {
                return new StartService();
            }
            case help -> {
                return new HelpService();
            }
            case register -> {
                return registrationService;
            }
            case test -> {
                return new TestService();
            }
            case getTrack -> {
                return getTrackService;
            }
            case uploadAlbum -> {
                return uploadTrackService;
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


        for (int i = 0; i < Commands.values().length; i++) {
            str.append("Type ")
                    .append(Commands.values()[i].toString())
                    .append(" ")
                    .append(Commands.values()[i].getDescription())
                    .append("\n");
        }

        return str.toString();
    }
}
