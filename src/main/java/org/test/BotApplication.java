package org.test;

import jakarta.persistence.EntityManagerFactory;
import lombok.val;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.test.dao.implementation.UserDAO;
import org.test.entity.*;
import org.test.listeners.BankSlashCommandsListener;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

/*        UserDAO userDAO = context.getBean(UserDAO.class);
        Bag bag = new Bag();
        BagElement bagElement = new BagElement();
        bagElement.setOreAmount(10L);
        bagElement.setOreID(7L);

        List<BagElement> bagElements = new ArrayList<>();
        bagElements.add(bagElement);

        bag.setContent(bagElements);

        User user = User.builder().id(1).money(1000L).pick(new Pick()).helmet(new Helmet()).bag(bag).build();

        userDAO.update(0L, user);*/

        jda = JDABuilder.createDefault("")
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("TV"))
                .addEventListeners(context.getBean(BankSlashCommandsListener.class))
                .build()
                .awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("inventory", "Test the bot interaction")
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
