package com.dam1rka.TelegramBot.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long telegramId;

    private String username;
    private String phone;

    private LocalDateTime created;

    @OneToMany(fetch = FetchType.EAGER)
    private List<UserStatusEntity> statuses;

}
