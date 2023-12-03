package org.test.entity.user_elements;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pick {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Float oreMultiplayer;
    private Float rareOreProbability;

    public final static Float startOreMultiplayer = 1.0f;
    public final static Float oreMultiplayerPerUpgrade = 0.1f;

    public final static Float startRareOreProbability = 0.9f;
    public final static Float rareOreProbabilityPerUpgrade = -0.05f;
    public final static Long startUpgradePrice = 200L;

    public Integer getLevel()
    {
        return (int) ((oreMultiplayer - startOreMultiplayer) / oreMultiplayerPerUpgrade);
    }
}
