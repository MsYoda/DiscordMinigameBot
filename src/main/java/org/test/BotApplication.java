package org.test;

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
import org.test.dto.MineDTO;
import org.test.dto.ShopDTO;
import org.test.listeners.SlashCommandListener;
import org.test.services.Mine;
import org.test.services.Shop;

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

       /* UserDAO userDAO = context.getBean(UserDAO.class);
        User user = userDAO.get(1L);*/

        jda = JDABuilder.createDefault("")
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("TV"))
                .addEventListeners(context.getBean(SlashCommandListener.class))
                .build()
                .awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("mine", "Отправиться в шахту за новыми ресурсами"),
                Commands.slash("add_role", "Добавить роль в магазин")
                        .addOption(OptionType.ROLE, "role", "Роль для добавления в магазин")
                        .addOption(OptionType.INTEGER, "price", "Цена роли"),
                Commands.slash("buy_role", "Добавить роль в магазин")
                        .addOption(OptionType.ROLE, "role", "Покупаемая роль"),
                Commands.slash("roles_shop", "Список ролей для покупки")
        ).queue();

    }
}
