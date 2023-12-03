package org.test.entity.user_elements;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.test.entity.Ore;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bag")
public class Bag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer bagSize;

    @Transient
    private List<BagElement> content;

    public static Integer startBagSize = 10;
    public static Integer bagSizePerUpgrade = 2;
    public static Integer startUpgradePrice = 200;

    public Integer getLevel()
    {
        return (bagSize - startBagSize) / bagSizePerUpgrade;
    }
    public void addOre(Ore ore, Long count) throws Exception {
        Long oreAmount = content.stream().map(x -> x.getOreAmount())
                .reduce(0L, (a, b) -> a + b);

        if (oreAmount >= this.bagSize) throw new Exception();

        for (int i = 0; i < content.size(); i++)
        {
            if (Objects.equals(content.get(i).getOreID(), ore.getId())){
                content.get(i).setOreAmount(content.get(i).getOreAmount() + count);
                return;
            }
        }

        BagElement bagElement = BagElement.builder()
                .oreID(ore.getId())
                .oreAmount(Math.min(count, this.bagSize - oreAmount))
                .build();

        content.add(bagElement);
    }
}
