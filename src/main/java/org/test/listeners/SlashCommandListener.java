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
import org.test.entity.game.blackjack.BlackJackSession;
import org.test.entity.game.blackjack.Card;
import org.test.entity.game.blackjack.CardSuit;
import org.test.entity.game.hangman.HangmanSession;
import org.test.services.games.BlackJack;
import org.test.services.games.Hangman;
import org.test.services.games.Mine;
import org.test.services.games.Shop;
import org.test.utils.DiscordUtil;
import org.test.utils.TimeUtil;

import java.sql.SQLException;
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
    @Autowired
    private BlackJack blackJack;

    public MessageEmbed errorEmbed(String text){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ошибка");
        embedBuilder.addField("Причина", text, false);

        return embedBuilder.build();
    }
    public void doMine(SlashCommandInteractionEvent event){
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
                return ;
            }
            if (mineDTO.isUserDead())
            {
                embedBuilder.addField("Реузльтат", "Пещера обрушилась и вы с трудом выбрались из-под завала\n" +
                                                                "Ваша каска не спасла вас и вы потеряли все полученные ресурсы", false);
                event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                return;
            }

            long sum = 0L;
            for (OreDTO x : mineDTO.getOreDTOList())
            {
                embedBuilder.addField(x.getName(), x.getAmount().toString() + " x " + x.getPrice() + " = " + x.getPrice() * x.getAmount() + " монет", false);
                sum += x.getPrice() * x.getAmount();
            }

            embedBuilder.addField("Сумма", sum + " монет", false);

        }
        catch (SQLException e) {}

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }
    public void doAddRole(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            errorEmbed("Отсутствуют права");
            return;
        }
        Role role = new Role();
        role.setId(event.getOption("role").getAsRole().getIdLong());
        role.setPrice(event.getOption("price").getAsLong());

        try {
            shop.addRoleToShop(role);
        }
        catch (Exception e) {
            if (e instanceof SQLException)
            {
                event.getHook().editOriginalEmbeds(errorEmbed("Сервер не доступен")).queue();
                return;
            }
            event.getHook().editOriginalEmbeds(errorEmbed(e.getMessage())).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Вызов команды");
        embedBuilder.addField("Результат", "Роль добавлена", false);

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        embedBuilder.build();
    }

    public void doBuyRole(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

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
                event.getHook().editOriginalEmbeds(errorEmbed("Сервер не доступен")).queue();
                return ;
            }
            event.getHook().editOriginalEmbeds(errorEmbed(e.getMessage())).queue();
            return ;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Вызов команды");
        embedBuilder.addField("Результат", "Роль куплена", false);

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    public void doRolesShop(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Guild guild = event.getMember().getGuild();
        List<Role> roles = null;
        try {
            roles = shop.getAllRoles();
        }
        catch (SQLException e)
        {
            event.getHook().editOriginalEmbeds(errorEmbed("Сервер не доступен")).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Список доступных для покупки ролей");

        for (Role role : roles) {
            embedBuilder.addField(DiscordUtil.getRoleById(role.getId().toString(), guild).getName() , role.getPrice() + " монет", false);
        }

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }
    public void doUpdateRole(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR))
        {
            event.getHook().editOriginalEmbeds(errorEmbed("Отсутствуют права")).queue();
            return ;
        }

        Long newPrice = event.getOption("price").getAsLong();

        try {
            shop.updateRole(event.getOption("role").getAsRole().getIdLong(), newPrice);
        }
        catch (SQLException e)
        {
            event.getHook().editOriginalEmbeds(errorEmbed("Сервер не доступен")).queue();
            return ;
        }
        catch (NoSuchElementException e)
        {
            event.getHook().editOriginalEmbeds(errorEmbed("Такой роли не существует")).queue();
            return ;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Вызов команды");
        embedBuilder.addField("Результат", "Роль обновлена", false);

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    public void doDeleteRole(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR))
        {
            event.getHook().editOriginalEmbeds(errorEmbed("Отсутствуют права")).queue();
            return ;
        }
        try {
            shop.deleteRole(event.getOption("role").getAsRole().getIdLong());
        }
        catch (SQLException e)
        {
            event.getHook().editOriginalEmbeds(errorEmbed("Сервер не доступен")).queue();
            return ;
        }
        catch (NoSuchElementException e)
        {
            event.getHook().editOriginalEmbeds(errorEmbed("Такой роли не существует")).queue();
            return ;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Вызов команды");
        embedBuilder.addField("Результат", "Роль удалена", false);

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    public void doHangman(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        HangmanSession session = hangman.runActivity(event.getHook(), Long.valueOf(event.getMember().getId()));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Виселица");
        embedBuilder.addField("Правила", "Вводите по одной русской букве и нажимайте Enter\nВведите y, чтобы начать игру", false);
        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    public void doUpgrade(SlashCommandInteractionEvent event){

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
            event.getHook().editOriginalEmbeds(errorEmbed("Сервер не доступен")).queue();
            return ;
        }
        catch (NoSuchElementException e)
        {
            event.getHook().editOriginalEmbeds(errorEmbed("Вы не зарегистроваын в системе!")).queue();
            return ;
        }

        event.replyEmbeds(embedBuilder.build()).setActionRow(
                Button.secondary("up_pick", Emoji.fromUnicode("⛏\uFE0F")),
                Button.secondary("up_helmet", Emoji.fromUnicode("⛑\uFE0F")),
                Button.secondary("up_bag", Emoji.fromUnicode("\uD83D\uDCB0"))
        ).setEphemeral(true).queue();
    }

    public void doBlackJack(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Long userID = event.getMember().getIdLong();
        Long bet = event.getOption("bet").getAsLong();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Блэк-джэк");

        BlackJackSession blackJackSession = null;
        try {
            blackJackSession = blackJack.runActivity(userID, bet);
        } catch (Exception e) {
            if (e instanceof SQLException)
            {
                event.getHook().editOriginalEmbeds(errorEmbed("Сервер не доступен")).queue();
                return;
            }
            event.getHook().editOriginalEmbeds(errorEmbed(e.getMessage())).queue();
            return;
        }


        event.getHook().editOriginalEmbeds(getBlackJackEmbed(blackJackSession, false).build()).setActionRow(
                Button.secondary("bj_take", "Взять"),
                Button.secondary("bj_enough","Хватит")
        ).queue();

    }

    public void doInventory(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        Long userID = event.getMember().getIdLong();
        User user = null;
        try {
            user = userDAO.get(userID).orElseThrow();
        }
        catch (SQLException e)
        {
            event.getHook().editOriginalEmbeds(errorEmbed("Сервер не доступен")).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Инвентарь");
        embedBuilder.setThumbnail(event.getMember().getAvatarUrl());

        embedBuilder.addField("Монеты", user.getMoney() + " штук", false);
        embedBuilder.addField("Кирка", user.getPick().getLevel() + " уровня", false);
        embedBuilder.addField("Шлем", user.getHelmet().getLevel() + " уровня", false);
        embedBuilder.addField("Рюкзак", user.getBag().getLevel() + " уровня", false);

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    public void upgradeMenuButtonProcess(ButtonInteractionEvent event)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (event.getComponentId().equals("up_pick")) {
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

        }
        else if (event.getComponentId().equals("up_helmet")) {
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
        else if (event.getComponentId().equals("up_bag")) {
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

    private EmbedBuilder getBlackJackEmbed(BlackJackSession session, boolean showBotHand)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Блэк-джэк");

        if (!showBotHand)
        {
            embedBuilder.addField("Рука бота", "? ".repeat(session.getBotDeck().size()), false);
        }
        else
        {
            StringBuilder stringBuilder = new StringBuilder();
            for (Card card: session.getBotDeck())
            {
                stringBuilder.append(card.getCard()).append(CardSuit.getSymbol(card.getSuit())).append(' ');
            }
            embedBuilder.addField("Рука бота", stringBuilder.toString(), false);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Card card: session.getUserDeck())
        {
            stringBuilder.append(card.getCard()).append(CardSuit.getSymbol(card.getSuit())).append(' ');
        }
        embedBuilder.addField("Ваша рука", stringBuilder.toString(), false);
        return embedBuilder;
    }
    public void blackJackButtonProcess(ButtonInteractionEvent event)
    {
        BlackJackSession session = null;
        Long userID = event.getMember().getIdLong();

        if (!blackJack.isSessionExist(userID)) return;

        if (event.getComponentId().equals("bj_take")) {
            blackJack.deal(userID);
            session = blackJack.botThinks(userID);
        }
        else if (event.getComponentId().equals("bj_enough")){
            while (!blackJack.isGameOver(userID)){
                session = blackJack.botThinks(userID);
            }
        }
        if (blackJack.isGameOver(userID)) {
            EmbedBuilder embedBuilder = getBlackJackEmbed(session, true);
            if (blackJack.isUserWin(userID)){
                User user = null;
                try {
                    user = userDAO.get(userID).get();
                    user.setMoney(user.getMoney() + session.getBet() * BlackJack.winMultiplayer);
                    userDAO.update(user);
                } catch (SQLException e) {
                    event.editMessageEmbeds(errorEmbed("Сервер недоступен")).queue();
                }
                embedBuilder.setFooter("Вы победили! Ваш выйгрыш: " + session.getBet() * BlackJack.winMultiplayer + " монет");
            }
            else {
                embedBuilder.setFooter("Вы проиграли((");
            }
            event.editMessageEmbeds(embedBuilder.build()).queue();
            blackJack.deleteSession(userID);
        }
        else{
            EmbedBuilder embedBuilder = getBlackJackEmbed(session, false);
            event.editMessageEmbeds(embedBuilder.build()).queue();
        }
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("up")) upgradeMenuButtonProcess(event);
        else if (event.getComponentId().startsWith("bj")) blackJackButtonProcess(event);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
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
            case "mine" -> doMine(event);
            case "add_role" -> doAddRole(event);
            case "buy_role" -> doBuyRole(event);
            case "roles_shop" -> doRolesShop(event);
            case "update_role" -> doUpdateRole(event);
            case "delete_role" -> doDeleteRole(event);
            case "hangman" -> doHangman(event);
            case "upgrade_item" -> doUpgrade(event);
            case "black_jack" -> doBlackJack(event);
            case "inventory" -> doInventory(event);
        }

    }
}
