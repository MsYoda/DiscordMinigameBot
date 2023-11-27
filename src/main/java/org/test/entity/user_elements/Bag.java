package org.test.entity.user_elements;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.test.entity.Ore;

import java.util.List;

@Data
@Entity
@Table(name = "bag")
public class Bag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer bagSize;

    @Transient
    private List<BagElement> content;

    public void addOre(Ore ore, Long count) throws Exception {
        Long oreAmount = content.stream().map(x -> x.getOreAmount())
                .reduce(0L, (a, b) -> a + b);

        if (oreAmount >= this.bagSize) throw new Exception();

        BagElement bagElement = BagElement.builder()
                .oreID(ore.getId())
                .oreAmount(Math.min(count, this.bagSize - oreAmount))
                .build();

        content.add(bagElement);
    }
}
