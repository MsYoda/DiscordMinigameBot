package org.test;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.hibernate.SessionFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.test.dao.implementation.CooldownDAO;
import org.test.entity.Cooldown;
import org.test.listeners.HangmanMessageListener;
import org.test.listeners.SlashCommandListener;
import org.test.utils.MathUtil;

import java.io.Serial;

@Configuration
@ComponentScan
public class BotApplication {
    private static JDA jda;

    @Bean
    public SessionFactory sessionFactory()
    {
        return new org.hibernate.cfg.Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }


    public static void main(String[] args) throws Exception
    {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BotApplication.class);

        jda = JDABuilder.createDefault("")
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.listening("ваши команды"))
                .addEventListeners(context.getBean(SlashCommandListener.class))
                .addEventListeners(context.getBean(HangmanMessageListener.class))
                .build()
                .awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("mine", "Отправиться в шахту за новыми ресурсами"),
                Commands.slash("add_role", "Добавить роль в магазин")
                        .addOption(OptionType.ROLE, "role", "Роль для добавления в магазин")
                        .addOption(OptionType.INTEGER, "price", "Цена роли")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                Commands.slash("buy_role", "Добавить роль в магазин")
                        .addOption(OptionType.ROLE, "role", "Покупаемая роль"),
                Commands.slash("roles_shop", "Список ролей для покупки"),
                Commands.slash("update_role", "Изменить цену роли")
                        .addOption(OptionType.ROLE, "role", "Роль для обновления")
                        .addOption(OptionType.INTEGER, "price", "Новая цена")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                Commands.slash("delete_role", "Удалить роль")
                        .addOption(OptionType.ROLE, "role", "Удаляемая роль")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                Commands.slash("hangman", "Сыграть в виселицу"),
                Commands.slash("black_jack", "Сыграть в блэк-джэк")
                        .addOption(OptionType.INTEGER, "bet", "Ставка на игру"),
                Commands.slash("upgrade_item", "Меню улучшения"),
                Commands.slash("inventory", "Изучить свой профиль")
        ).queue();

    }
}
