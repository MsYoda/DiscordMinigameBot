package org.test.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.entity.minigames.HangmanSession;
import org.test.services.Hangman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Service
public class HangmanMessageListener extends ListenerAdapter {
    @Autowired
    private Hangman hangman;

    private final HashMap<Integer, String> errorImageURLs = new HashMap<>();

    {
        errorImageURLs.put(0, "https://cdn.discordapp.com/attachments/619631064067080230/1179169271381766227/image.png?ex=6578cdf6&is=656658f6&hm=139a7ca5d4f3c65007cd9a07e6600b8c04389e9f10da9ad7c8ce9220a2048828&");
        errorImageURLs.put(1, "https://cdn.discordapp.com/attachments/1179171824198758460/1179171997205418055/1.png?ex=6578d080&is=65665b80&hm=92b6555d819a7379a8a0715cc1aec3c947e71805aefdf90714ac60f8d9dde386&");
        errorImageURLs.put(2, "https://cdn.discordapp.com/attachments/1179171824198758460/1179171997427695676/2.png?ex=6578d080&is=65665b80&hm=785394a0ddc94a659fb3a1cd9a94065a13877b314bcb025a2cb2afab66099d96&");
        errorImageURLs.put(3, "https://cdn.discordapp.com/attachments/1179171824198758460/1179171997666775141/3.png?ex=6578d080&is=65665b80&hm=1f5f6520e12cdb86736ea9ea740c29ef93578dbff87c1abbaf1e90066725e1d2&");
        errorImageURLs.put(4, "https://cdn.discordapp.com/attachments/1179171824198758460/1179171997960372314/4.png?ex=6578d080&is=65665b80&hm=c4f69db6d0ad8a6b552225a3f9b815756b92dc414406a064e9f372d98fe6d3ca&");
        errorImageURLs.put(5, "https://cdn.discordapp.com/attachments/1179171824198758460/1179171998199451707/5.png?ex=6578d080&is=65665b80&hm=ebaf4a877aae40a51ba11c3f8fc179f6eeabaeb8e5d3f4dee66c979549d47e08&");
        errorImageURLs.put(6, "https://cdn.discordapp.com/attachments/1179171824198758460/1179171998442737825/6.png?ex=6578d080&is=65665b80&hm=7503e9701a53f4d9e1344cfdab599584069e853bd41c42da94bc1a494f9d7fac&");
        errorImageURLs.put(7, "https://cdn.discordapp.com/attachments/1179171824198758460/1179171998786654289/7.png?ex=6578d080&is=65665b80&hm=f8cfa7bad38596417c77213a94bba010495175f9ac23fd55de592f3718c22bec&");
        errorImageURLs.put(8, "https://cdn.discordapp.com/attachments/1179171824198758460/1179171999042523296/8.png?ex=6578d080&is=65665b80&hm=3edc79a4a0b0e6f9f4f861e2c34c5f794f0ba42c0e056a1280120d8f7739a272&");
    }

    public EmbedBuilder getHangmanEmbed(HangmanSession session)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Виселица");
        embedBuilder.setImage(errorImageURLs.get(Integer.valueOf(session.getErrorCount())));
        embedBuilder.addField("Тема", session.getHint(), true);
        StringBuilder displayedGuessText = new StringBuilder();

        for (Character ch : session.getGuess().toCharArray())
        {
            displayedGuessText.append(ch).append(' ');
        }

        embedBuilder.addField("Угадано", displayedGuessText.toString(), true);
        return embedBuilder;
    }
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Long userID = event.getMember().getIdLong();

        if (hangman.getSession(userID).isPresent())
        {
            Optional<InteractionHook> interactionHookOpt = hangman.getInteraction(userID);
            if (interactionHookOpt.isEmpty()) return;
            InteractionHook interactionHook = interactionHookOpt.get();
            if (interactionHook.isExpired())
            {
                hangman.deleteSession(userID);
                event.getChannel().sendMessage(event.getMember().getAsMention() + " время твоей игрвоой сессии истекло!").queue();
                return;
            }

            EmbedBuilder embedBuilder = null;
            if (event.getMessage().getContentDisplay().toLowerCase().charAt(0) == 'y')
            {
                embedBuilder = getHangmanEmbed(hangman.getSession(userID).get());
            }
            else if (event.getMessage().getContentDisplay().length() == 1)
            {
                HangmanSession session = hangman.guess(event.getMessage().getContentDisplay().toLowerCase().charAt(0), userID);
                embedBuilder = getHangmanEmbed(session);
            }
            boolean endOfGame = false;
            if (hangman.isUserWin(userID))
            {
                embedBuilder.setFooter("Вы победили!");
                endOfGame = true;
            }
            if (hangman.isUserLoose(userID))
            {
                embedBuilder.setFooter("Вы проиграли((");
                endOfGame = true;
            }
            if (endOfGame)
            {
                hangman.deleteSession(userID);
            }

            interactionHook.editOriginalEmbeds(embedBuilder.build()).queue();
            event.getMessage().delete().queue();
        }
    }
}
