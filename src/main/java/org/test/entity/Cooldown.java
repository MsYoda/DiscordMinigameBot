package org.test.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "cooldown")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cooldown {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime endTime;

    @Column(name = "command_id")
    private CommandID commandID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
