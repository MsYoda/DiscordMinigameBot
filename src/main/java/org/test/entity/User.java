package org.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.springframework.stereotype.Repository;



@Entity
@Table(name = "user_db")
@Data
@Builder
public class User {
    @Id
    private Integer id;
    private Long money;

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Bag bag;

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Helmet helmet;

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Pick pick;
}
