package org.test.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.config.BotLanguageConfig;
import org.test.services.BankService;

@Service
public class BankSlashCommandsListener extends ListenerAdapter {
    @Autowired
    private BankService bankService;

    private String addMoney(SlashCommandInteractionEvent event)
    {
        String botReply = BotLanguageConfig.addMoneySucceed;
        if (event.getOption(BotLanguageConfig.userParamName) != null && event.getOption(BotLanguageConfig.moneyParamName) != null) {
            try {
                String username = event.getOption(BotLanguageConfig.userParamName).getAsString();
                Integer moneyToAdd = event.getOption(BotLanguageConfig.moneyParamName).getAsInt();

                bankService.setUserMoney(username,bankService.getUserMoney(username) + moneyToAdd );
                botReply = botReply
                        .replace("$(money)", String.valueOf(moneyToAdd))
                        .replace("$(user)", "<@" + username + ">");
            }
            catch (NullPointerException exception) {
                System.out.println(exception.getMessage());
                botReply = BotLanguageConfig.invalidArgumentsType;
            }
            catch (Exception exception) {
                System.out.println(exception.getMessage());
                botReply = exception.getMessage();
            }
        }
        else botReply = BotLanguageConfig.invalidArgumentsCount;

        return botReply;
    }
    private String knowBalance(SlashCommandInteractionEvent event)
    {
        String botReply = BotLanguageConfig.knowBalanceSucceed;
        String username;
            try {
                if (event.getOption(BotLanguageConfig.userParamName) != null) {
                    username = event.getOption(BotLanguageConfig.userParamName).getAsString();
                }
                else {
                    username = event.getMember().getId();
                }
                Integer money = bankService.getUserMoney(username);
                botReply = botReply
                        .replace("$(money)", String.valueOf(money))
                        .replace("$(user)", "<@" + username + ">");
            }
            catch (NullPointerException exception) {
                System.out.println(exception.getMessage());
                botReply = BotLanguageConfig.invalidArgumentsType;
            }
            catch (Exception exception) {
                System.out.println(exception.getMessage());
                botReply = exception.getMessage();
            }
        return botReply;
    }
    private String removeMoney(SlashCommandInteractionEvent event) {
        String botReply = BotLanguageConfig.removeMoneySucceed;
        if (event.getOption(BotLanguageConfig.userParamName) != null && event.getOption(BotLanguageConfig.moneyParamName) != null) {
            try {
                String username = event.getOption(BotLanguageConfig.userParamName).getAsString();
                Integer moneyToRemove = event.getOption(BotLanguageConfig.moneyParamName).getAsInt();
                Integer userResultMoney = bankService.getUserMoney(username) - moneyToRemove;

                if (userResultMoney < 0)
                    throw new Exception(
                            BotLanguageConfig.userDoesntHaveMoney
                                    .replace("$(user)", "<@" + username + ">")
                                    .replace("$(money)", String.valueOf(bankService.getUserMoney(username)))
                    );

                bankService.setUserMoney(username,bankService.getUserMoney(username) - moneyToRemove );
                botReply = botReply
                        .replace("$(money)", String.valueOf(moneyToRemove))
                        .replace("$(user)", "<@" + username + ">");
            }
            catch (NullPointerException exception) {
                System.out.println(exception.getMessage());
                botReply = BotLanguageConfig.invalidArgumentsType;
            }
            catch (Exception exception) {
                System.out.println(exception.getMessage());
                botReply = exception.getMessage();
            }
        }
        else botReply = BotLanguageConfig.invalidArgumentsCount;

        return botReply;
    }

    private String transferMoney(SlashCommandInteractionEvent event) {
        String botReply = BotLanguageConfig.transferMoneySucceed;
        if (event.getOption(BotLanguageConfig.destinationUserParamName) != null && event.getOption(BotLanguageConfig.moneyParamName) != null) {
            try {
                String sourceUsername = event.getMember().getId();
                String destinationUsername = event.getOption(BotLanguageConfig.destinationUserParamName).getAsString();
                Integer moneyToSend = event.getOption(BotLanguageConfig.moneyParamName).getAsInt();

                if (bankService.getUserMoney(sourceUsername) < moneyToSend)
                    throw new Exception(
                            BotLanguageConfig.userDoesntHaveMoney
                                    .replace("$(user)", "<@" + sourceUsername + ">")
                                    .replace("$(money)", String.valueOf(bankService.getUserMoney(sourceUsername)))
                    );

                bankService.setUserMoney(sourceUsername, bankService.getUserMoney(sourceUsername) - moneyToSend);
                bankService.setUserMoney(destinationUsername, bankService.getUserMoney(destinationUsername) + moneyToSend);

                botReply = botReply
                        .replace("$(money)", String.valueOf(moneyToSend))
                        .replace("$(source-user)", "<@" + sourceUsername + ">")
                        .replace("$(destination-user)", "<@" + destinationUsername + ">");
            }
            catch (NullPointerException exception) {
                System.out.println(exception.getMessage());
                botReply = BotLanguageConfig.invalidArgumentsType;
            }
            catch (Exception exception) {
                System.out.println(exception.getMessage());
                botReply = exception.getMessage();
            }
        }
        else botReply = BotLanguageConfig.invalidArgumentsCount;
        return botReply;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        String botReply = "Unknown command";

        switch (event.getName()) {
            case BotLanguageConfig.knowBalanceCommandName -> botReply = knowBalance(event);
            case BotLanguageConfig.addMoneyCommandName -> botReply = addMoney(event);
            case BotLanguageConfig.removeMoneyCommandName -> botReply = removeMoney(event);
            case BotLanguageConfig.transferMoneyCommandName -> botReply = transferMoney(event);
        }
        event.reply(botReply).setEphemeral(true).queue();
    }
}
