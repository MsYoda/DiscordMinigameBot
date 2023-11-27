package org.test.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ore")
@Data
public class Ore {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Float rarity;
    private Integer price;

    public int compare(Ore a, Ore b){

        return a.getRarity().compareTo(b.getRarity());
    }
}
