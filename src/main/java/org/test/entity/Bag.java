package org.test.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Data
@Entity
@Table(name = "bag")
public class Bag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer bagSize;

    @OneToMany
    @JoinColumn(name = "bag_id")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<BagElement> content;
}
