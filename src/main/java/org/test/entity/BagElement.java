package org.test.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bag_element")
public class BagElement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long oreID;
    private Long oreAmount;


}
