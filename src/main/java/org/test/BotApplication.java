package org.test;

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

        jda = JDABuilder.createDefault("MTEzOTYzMDA3NjU5ODY5Mzg5OA.GMQZdL.Ifg6OklghqhmOB5tcJbRQcARU3u2pDGB0TRj0Y")
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("TV"))
                .addEventListeners(context.getBean(SlashCommandListener.class))
                .build()
                .awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("mine", "Отправиться в шахту за новыми ресурсами")
                /*Commands.slash(BotLanguageConfig.addMoneyCommandName, "Gives money to the user")
                        .addOption(OptionType.USER, BotLanguageConfig.userParamName, "User's balance will be increased for 'money' value")
                        .addOption(OptionType.INTEGER, BotLanguageConfig.moneyParamName, "Money to add to user balance"),
                Commands.slash(BotLanguageConfig.removeMoneyCommandName, "Removes money from the user")
                        .addOption(OptionType.USER, BotLanguageConfig.userParamName, "User's balance will be decreased for 'money' value")
                        .addOption(OptionType.INTEGER, BotLanguageConfig.moneyParamName, "Money to remove from user's balance"),
                Commands.slash(BotLanguageConfig.transferMoneyCommandName, "Sends money from source-user to destination-user")
                        .addOption(OptionType.USER, BotLanguageConfig.destinationUserParamName, "Will receive money")
                        .addOption(OptionType.INTEGER, BotLanguageConfig.moneyParamName, "Money to send")*/
        ).queue();

    }
}
