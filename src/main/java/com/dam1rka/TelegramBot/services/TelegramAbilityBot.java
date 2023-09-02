package com.dam1rka.TelegramBot.services;

import com.dam1rka.TelegramBot.config.BotConfig;
import com.dam1rka.TelegramBot.models.BotServices;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.objects.ReplyFlow;

import static org.telegram.abilitybots.api.objects.Locality.ALL;

import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;


@Service
public class TelegramAbilityBot extends AbilityBot {
    private final BotConfig config;

    public TelegramAbilityBot(BotConfig config) {
        super(config.getBotToken(), config.getBotName());
        this.config = config;
    }

    @Override
    public long creatorId() {
        return config.getCreatorId();
    }

    public Ability sayHelloWorld() {
        return Ability
                .builder()
                .name("hello")
                .info("says hello world!")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("Hello world!", ctx.chatId()))
                .post(ctx -> silent.send("Bye world!", ctx.chatId()))
                .build();
    }

    public Ability help() {
        return Ability
                .builder()
                .name("help")
                .info("print help info")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send(BotServices.getHelpText(), ctx.chatId()))
                .build();
    }

    public Ability start() {
        return Ability
                .builder()
                .name("start")
                .info("print start")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send(
                        EmojiParser.parseToUnicode("Hi, " + ctx.user().getUserName() + ", nice to meet you!" + " :blush:"),
                        ctx.chatId()))
                .build();
    }

    public ReplyFlow sayTest() {
        Reply saidLeft = Reply.of((baseAbilityBot, update) ->
                        silent.send("Sir, I have gone left.", getChatId(update)), update -> update.getMessage().getText().equals("left")
                );

        Reply saidRight = Reply.of((baseAbilityBot, update) ->
                silent.send("Sir, I have gone right.", getChatId(update)), update -> update.getMessage().getText().equals("right")
        );

        return ReplyFlow.builder(db)
                // Just like replies, a ReplyFlow can take an action, here we want to send a
                // statement to prompt the user for directions!
                .action((baseAbilityBot, update) -> silent.send("Command me to go left or right!", getChatId(update)))
                // We should only trigger this flow when the user says "wake up"
                .onlyIf(update -> update.getMessage().getText().equals("wake up"))
                // The next method takes in an object of type Reply.
                // Here we chain our replies together
                .next(saidLeft)
                // We chain one more reply, which is when the user commands your bot to go right
                .next(saidRight)
                // Finally, we build our ReplyFlow
                .build();
    }

}
