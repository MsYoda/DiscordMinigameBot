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
public class Helmet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer toughness;
    private Integer lightPower;
    private Integer maxToughness;

    public static Integer startToughness = 50;
    public static Integer toughnessPerUpgrade = 20;

    public static Integer startLightPower = 1;
    public static Integer lightPowerPerUpgrade = 1;

    public static Integer startUpgradePrice = 400;

    public Integer getLevel()
    {
        return (maxToughness - startToughness) / toughnessPerUpgrade;
    }
}
