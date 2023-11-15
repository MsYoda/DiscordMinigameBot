package org.test.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.config.BotLanguageConfig;

@Service
public class BankSlashCommandsListener extends ListenerAdapter {
    @Autowired
    private SessionFactory sessionFactory;

    private Integer t = 1;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        String botReply = "Unknown command";

/*        switch (event.getName()) {
            case BotLanguageConfig.knowBalanceCommandName -> botReply = knowBalance(event);
            case BotLanguageConfig.addMoneyCommandName -> botReply = addMoney(event);
            case BotLanguageConfig.removeMoneyCommandName -> botReply = removeMoney(event);
            case BotLanguageConfig.transferMoneyCommandName -> botReply = transferMoney(event);
        }*/

        event.reply(botReply).setEphemeral(true).queue();
    }
}
