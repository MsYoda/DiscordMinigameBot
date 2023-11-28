package org.test.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.implementation.OreDAO;
import org.test.dao.implementation.UserDAO;
import org.test.dto.MineDTO;
import org.test.dto.OreDTO;
import org.test.dto.ShopDTO;
import org.test.entity.Role;
import org.test.services.Mine;
import org.test.services.Shop;
import org.test.utils.DiscordUtil;

import java.sql.SQLException;
import java.util.List;

@Service
public class SlashCommandListener extends ListenerAdapter {
    @Autowired
    private Mine mine;

    @Autowired
    private Shop shop;

    @Autowired
    private UserDAO userDAO;

    public MessageEmbed errorEmbed(String text){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ошибка");
        embedBuilder.addField("Причина", text, false);
        return embedBuilder.build();
    }
    public MessageEmbed doMine(SlashCommandInteractionEvent event){
        Long userID = Long.valueOf(event.getMember().getId());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Результат похода в шахту");
        embedBuilder.setFooter(event.getUser().getAsTag(), event.getUser().getAvatarUrl());

        try {
            MineDTO mineDTO = mine.runActivity(userID);

            Long sum = 0L;
            for (OreDTO x : mineDTO.getOreDTOList())
            {
                embedBuilder.addField(x.getName(), x.getAmount().toString() + " x " + x.getPrice() + " = " + x.getPrice() * x.getAmount() + " монет", false);
                sum += x.getPrice() * x.getAmount();
            }

            embedBuilder.addField("Сумма", sum.toString() + " монет", false);

        }
        catch (SQLException e) {}


        return embedBuilder.build();
    }
    public MessageEmbed doAddRole(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return errorEmbed("Отсутствуют права");
        Role role = new Role();
        role.setId(event.getOption("role").getAsRole().getIdLong());
        role.setPrice(event.getOption("price").getAsLong());

        try {
            shop.addRoleToShop(role);
        }
        catch (Exception e) {
            if (e instanceof SQLException)
            {
                return errorEmbed("Проблемы с сервером");
            }
            return errorEmbed(e.getMessage());
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Вызов команды");
        embedBuilder.addField("Результат", "Роль добавлена", false);

        return embedBuilder.build();
    }

    public MessageEmbed doBuyRole(SlashCommandInteractionEvent event) {

        Long roleID = event.getOption("role").getAsRole().getIdLong();
        Long userID = Long.valueOf(event.getMember().getId());
        Guild guild = event.getMember().getGuild();

        try {
            shop.buyRole(userID, roleID);
            DiscordUtil.addRolToMember(roleID, userID, guild);
        }
        catch (Exception e)
        {
            if (e instanceof SQLException)
            {
                return errorEmbed("Проблемы с сервером");
            }
            return errorEmbed(e.getMessage());
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Вызов команды");
        embedBuilder.addField("Результат", "Роль куплена", false);

        return embedBuilder.build();
    }

    public MessageEmbed doRolesShop(SlashCommandInteractionEvent event) {
        Guild guild = event.getMember().getGuild();
        List<Role> roles = null;
        try {
            roles = shop.getAllRoles();
        }
        catch (SQLException e)
        {
            return errorEmbed("Сервер не доступен");
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Список доступных для покупки ролей");

        for (Role role : roles) {
            embedBuilder.addField(DiscordUtil.getRoleById(role.getId().toString(), guild).getName() , role.getPrice() + " монет", false);
        }


        return embedBuilder.build();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        MessageEmbed botReply = null;
        Long userID = Long.valueOf(event.getMember().getId());
        try {
            userDAO.get(userID);
        } catch (SQLException e) {
            try {
                userDAO.addEmptyUser(userID);
            } catch (SQLException ex) {
                return;
            }
        }
        switch (event.getName()) {
            case "mine" -> botReply = doMine(event);
            case "add_role" -> botReply = doAddRole(event);
            case "buy_role" -> botReply = doBuyRole(event);
            case "roles_shop" -> botReply = doRolesShop(event);
        }

        event.getHook().editOriginalEmbeds(botReply).queue();
    }
}
