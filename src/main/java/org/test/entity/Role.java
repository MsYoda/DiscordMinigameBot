package org.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.test.entity.user_elements.Bag;
import org.test.entity.user_elements.Helmet;
import org.test.entity.user_elements.Pick;


@Entity
@Table(name = "role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    @Id
    private Long id;
    private Long price;
}
