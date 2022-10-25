package com.dam1rka.TelegramBot.repositories;

import com.dam1rka.TelegramBot.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByTelegramId(Long id);
}
