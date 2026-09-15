package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.NotificationTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationTemplateRepository
        extends JpaRepository<NotificationTemplateEntity, UUID> {

    Optional<NotificationTemplateEntity> findByTemplateCodeAndChannelAndLocale(
            String templateCode,
            String channel,
            String locale
    );
}