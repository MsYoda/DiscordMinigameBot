package org.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.test.entity.user_elements.Bag;
import org.test.entity.user_elements.Helmet;
import org.test.entity.user_elements.Pick;


@Entity
@Table(name = "role")
@Data
@Builder
public class Role {
    @Id
    private Integer id;
    private Long price;
}
