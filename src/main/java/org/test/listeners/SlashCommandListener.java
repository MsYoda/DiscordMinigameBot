package org.test.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.implementation.CooldownDAO;
import org.test.dao.implementation.UserDAO;
import org.test.dto.MineDTO;
import org.test.dto.OreDTO;
import org.test.entity.Role;
import org.test.entity.User;
import org.test.entity.game.HangmanSession;
import org.test.services.games.Hangman;
import org.test.services.games.Mine;
import org.test.services.games.Shop;
import org.test.utils.DiscordUtil;
import org.test.utils.TimeUtil;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SlashCommandListener extends ListenerAdapter {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private CooldownDAO cooldownDAO;
    @Autowired
    private Mine mine;
    @Autowired
    private Shop shop;
    @Autowired
    private Hangman hangman;

    public MessageEmbed errorEmbed(String text){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ошибка");
        embedBuilder.addField("Причина", text, false);

        return embedBuilder.build();
    }
    public MessageEmbed doMine(SlashCommandInteractionEvent event){
        event.deferReply().queue();
        Long userID = Long.valueOf(event.getMember().getId());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Результат похода в шахту");
        embedBuilder.setFooter(event.getUser().getAsTag(), event.getUser().getAvatarUrl());

        try {
            MineDTO mineDTO = mine.runActivity(userID);

            if (mineDTO.isCooldownActive())
            {
                event.getHook().editOriginalEmbeds(errorEmbed("Время отката команды ещё не прошло\n" +
                        "Осталось: " + TimeUtil.getDurationInString(LocalDateTime.now(), mineDTO.getEndTime()))).queue();
                return embedBuilder.build();
            }
            if (mineDTO.isUserDead())
            {
                embedBuilder.addField("Реузльтат", "Пещера обрушилась и вы с трудом выбрались из-под завала\n" +
                                                                "Ваша каска не спасла вас и вы потеряли все полученные ресурсы", false);
                event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                return embedBuilder.build();
            }

            Long sum = 0L;
            for (OreDTO x : mineDTO.getOreDTOList())
            {
                embedBuilder.addField(x.getName(), x.getAmount().toString() + " x " + x.getPrice() + " = " + x.getPrice() * x.getAmount() + " монет", false);
                sum += x.getPrice() * x.getAmount();
            }

            embedBuilder.addField("Сумма", sum.toString() + " монет", false);

        }
        catch (SQLException e) {}

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
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

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
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

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
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

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        return embedBuilder.build();
    }
    public MessageEmbed doUpdateRole(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return errorEmbed("Отсутствуют права");
        Long newPrice = event.getOption("price").getAsLong();

        try {
            shop.updateRole(event.getOption("role").getAsRole().getIdLong(), newPrice);
        }
        catch (SQLException e)
        {
            return errorEmbed("Ошибка на сервере");
        }
        catch (NoSuchElementException e)
        {
            return errorEmbed("Такой роли не существует");
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Вызов команды");
        embedBuilder.addField("Результат", "Роль обновлена", false);

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        return embedBuilder.build();
    }

    public MessageEmbed doDeleteRole(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return errorEmbed("Отсутствуют права");
        try {
            shop.deleteRole(event.getOption("role").getAsRole().getIdLong());
        }
        catch (SQLException e)
        {
            return errorEmbed("Ошибка на сервере");
        }
        catch (NoSuchElementException e)
        {
            return errorEmbed("Такой роли не существует");
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Вызов команды");
        embedBuilder.addField("Результат", "Роль удалена", false);

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        return embedBuilder.build();
    }

    public MessageEmbed doHangman(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        HangmanSession session = hangman.runActivity(event.getHook(), Long.valueOf(event.getMember().getId()));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Виселица");
        embedBuilder.addField("Правила", "Вводите по одной русской букве и нажимайте Enter\nВведите y, чтобы начать игру", false);
        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        return embedBuilder.build();
    }

    public MessageEmbed doUpgrade(SlashCommandInteractionEvent event){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Меню улучшений");

        try {
            User user = userDAO.get(Long.valueOf(event.getMember().getId())).orElseThrow();
            StringBuilder builder = new StringBuilder();

            builder.append("Кирка (").append(user.getPick().getLevel()).append(" -> ").append(user.getPick().getLevel() + 1).append(")");
            embedBuilder.addField(builder.toString(), shop.getPickUpgradePrice(user) + " монет", false);
            builder = new StringBuilder();

            builder.append("Шлем (").append(user.getHelmet().getLevel()).append(" -> ").append(user.getHelmet().getLevel() + 1).append(")");
            embedBuilder.addField(builder.toString(), shop.getHelmetUpgradePrice(user) + " монет", false);
            builder = new StringBuilder();

            builder.append("Мешок (").append(user.getBag().getLevel()).append(" -> ").append(user.getBag().getLevel() + 1).append(")");
            embedBuilder.addField(builder.toString(), shop.getBagUpgradePrice(user) + " монет", false);
        }
        catch (SQLException e)
        {
            return errorEmbed("Ошибка на севрере!");
        }
        catch (NoSuchElementException e)
        {
            return errorEmbed("Вы не зарегистроваын в системе!");
        }

        event.replyEmbeds(embedBuilder.build()).setActionRow(
                Button.secondary("pick", Emoji.fromUnicode("⛏\uFE0F")),
                Button.secondary("helmet", Emoji.fromUnicode("⛑\uFE0F")),
                Button.secondary("bag", Emoji.fromUnicode("\uD83D\uDCB0"))
        ).setEphemeral(true).queue();

        return embedBuilder.build();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (event.getComponentId().equals("pick")) {
            embedBuilder.setTitle("Улчшение кирки");
            try {
                shop.upgradePick(event.getUser().getIdLong());
                embedBuilder.addField("Результат", "Успешно", false);
            } catch (Exception e) {
                if (e instanceof SQLException)
                {
                    event.editMessageEmbeds(errorEmbed("Ошибка сервера")).queue();
                    return;
                }
                event.editMessageEmbeds(errorEmbed(e.getMessage())).queue();
                return;
            }

        } else if (event.getComponentId().equals("helmet")) {
            embedBuilder.setTitle("Улчшение шлема");
            try {
                shop.upgradeHelmet(event.getUser().getIdLong());
                embedBuilder.addField("Результат", "Успешно", false);
            } catch (Exception e) {
                if (e instanceof SQLException)
                {
                    event.editMessageEmbeds(errorEmbed("Ошибка сервера")).queue();
                    return;
                }
                event.editMessageEmbeds(errorEmbed(e.getMessage())).queue();
                return;
            }
        }
        else if (event.getComponentId().equals("bag")) {
            embedBuilder.setTitle("Улчшение рюкзака");
            try {
                shop.upgradeBag(event.getUser().getIdLong());
                embedBuilder.addField("Результат", "Успешно", false);
            } catch (Exception e) {
                if (e instanceof SQLException)
                {
                    event.editMessageEmbeds(errorEmbed("Ошибка сервера")).queue();
                    return;
                }
                event.editMessageEmbeds(errorEmbed(e.getMessage())).queue();
                return;
            }
        }
        event.editMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
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
            case "update_role" -> botReply = doUpdateRole(event);
            case "delete_role" -> botReply = doDeleteRole(event);
            case "hangman" -> botReply = doHangman(event);
            case "upgrade_item" -> botReply = doUpgrade(event);
        }

    }
}
