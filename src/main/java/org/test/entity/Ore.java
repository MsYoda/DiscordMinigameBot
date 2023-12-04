package org.test.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ore")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
