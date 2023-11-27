package org.test.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "cooldown")
@Data
@Builder
public class Cooldown {
    @Id
    private Long id;

    private Date endTime;

    private Long commandID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
