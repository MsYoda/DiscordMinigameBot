package org.test;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.springframework.context.annotation.Bean;
import org.test.config.BotLanguageConfig;
import org.test.db.UserRepository;
import org.test.listeners.BankSlashCommandsListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class BotApplication {
    private static JDA jda;

    // Обязательно нужно перенастроить ссылку. После jdbc:sqlite: нужно указать полный путь до места, где будет хранится бд
    @Bean
    public UserRepository userRepository()
    {
        return new UserRepository(
                "jdbc:sqlite:/home/race/java/minecraftDiscordBot/MicecraftDiscordBot/src/main/resources/test.db",
                "user_money"
        );
    }
    public static void main(String[] args) throws Exception
    {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MinecraftBotApplication.class);

        jda = JDABuilder.createDefault("")
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("TV"))
                .addEventListeners(context.getBean(BankSlashCommandsListener.class))
                .build()
                .awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash(BotLanguageConfig.knowBalanceCommandName, "Test the bot interaction")
                        .addOption(OptionType.USER, BotLanguageConfig.userParamName, "User's balance will be printed"),
                Commands.slash(BotLanguageConfig.addMoneyCommandName, "Gives money to the user")
                        .addOption(OptionType.USER, BotLanguageConfig.userParamName, "User's balance will be increased for 'money' value")
                        .addOption(OptionType.INTEGER, BotLanguageConfig.moneyParamName, "Money to add to user balance"),
                Commands.slash(BotLanguageConfig.removeMoneyCommandName, "Removes money from the user")
                        .addOption(OptionType.USER, BotLanguageConfig.userParamName, "User's balance will be decreased for 'money' value")
                        .addOption(OptionType.INTEGER, BotLanguageConfig.moneyParamName, "Money to remove from user's balance"),
                Commands.slash(BotLanguageConfig.transferMoneyCommandName, "Sends money from source-user to destination-user")
                        .addOption(OptionType.USER, BotLanguageConfig.destinationUserParamName, "Will receive money")
                        .addOption(OptionType.INTEGER, BotLanguageConfig.moneyParamName, "Money to send")
        ).queue();
    }
}
