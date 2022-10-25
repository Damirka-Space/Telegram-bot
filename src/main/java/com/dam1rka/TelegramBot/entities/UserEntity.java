package com.dam1rka.TelegramBot.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long telegram_id;

    private String username;
    private String phone;

    private LocalDateTime created;

    @OneToMany(fetch = FetchType.EAGER)
    private List<UserStatusEntity> statuses;

}
