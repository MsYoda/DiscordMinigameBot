package org.test.entity.game.hangman;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.interactions.InteractionHook;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HangmanSession {
    private String answer;
    private String guess;
    private String hint;
    private Byte errorCount;
    private InteractionHook interactionHook;
}
