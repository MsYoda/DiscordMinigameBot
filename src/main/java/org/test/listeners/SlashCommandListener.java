package org.test.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dto.MineDTO;
import org.test.dto.ShopDTO;
import org.test.services.Mine;
import org.test.services.Shop;

import java.sql.SQLException;

@Service
public class SlashCommandListener extends ListenerAdapter {
    @Autowired
    private Mine mine;

    @Autowired
    private Shop shop;

    public String doMine(SlashCommandInteractionEvent event){
        String replay = "";
        Long userID = Long.valueOf(event.getUser().getId());

        try {
            MineDTO mineDTO = mine.runActivity(userID);
            ShopDTO shopDTO = shop.sellOre(mineDTO.getUserBag(), userID);
        }
        catch (SQLException e) {
            replay = "Серверная ошибка, пожайлуйста, обратитесть к администратору!";
        }


        return replay;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        String botReply = "Unknown command";
        switch (event.getName()) {
            case "mine" -> botReply = doMine(event);
        }

/*        switch (event.getName()) {
            case BotLanguageConfig.knowBalanceCommandName -> botReply = knowBalance(event);
            case BotLanguageConfig.addMoneyCommandName -> botReply = addMoney(event);
            case BotLanguageConfig.removeMoneyCommandName -> botReply = removeMoney(event);
            case BotLanguageConfig.transferMoneyCommandName -> botReply = transferMoney(event);
        }*/

        event.reply(botReply).setEphemeral(true).queue();
    }
}
