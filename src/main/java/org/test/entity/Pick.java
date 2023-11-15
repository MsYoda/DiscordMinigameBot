package org.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Pick {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Float oreMultiplayer;
    private Float rareOreProbability;
}
