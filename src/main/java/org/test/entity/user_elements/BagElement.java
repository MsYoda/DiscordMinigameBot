package org.test.entity.user_elements;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bag_element")
public class BagElement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long oreID;
    private Long oreAmount;


}
