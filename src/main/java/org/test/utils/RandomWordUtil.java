package org.test.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.test.entity.game.hangman.HangmanTopic;

import java.util.List;
import java.util.Objects;

@Component
public class RandomWordUtil {

    @Autowired
    private MathUtil mathUtil;

    private  final List<HangmanTopic> topics = List.of(
            HangmanTopic.builder()
                    .topic("Eда")
                    .words(List.of("яблоко", "мясо", "пирог", "картофель", "манго", "черника"))
                    .build(),
            HangmanTopic.builder()
                    .topic("Одежда")
                    .words(List.of("брюки", "носок", "кандибобер", "колготки", "лапоть", "валенок"))
                    .build(),
            HangmanTopic.builder()
                    .topic("Алкоголь")
                    .words(List.of("пиво", "виски", "коньяк", "вино", "портвейн", "сидр"))
                    .build()

    );
    public  HangmanTopic generateWord()
    {
        String topic = topics.get(mathUtil.getRandomInt(0, topics.size() - 1)).getTopic();
        List<String> words = topics.stream().filter(s -> Objects.equals(s.getTopic(), topic)).findFirst().get().getWords();
        String word = words.get(mathUtil.getRandomInt(0, words.size() - 1));
        return HangmanTopic.builder()
                .topic(topic)
                .words(List.of(word))
                .build();
    }
}
