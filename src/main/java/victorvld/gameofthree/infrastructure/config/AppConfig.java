package victorvld.gameofthree.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import victorvld.gameofthree.core.domain_models.GameState;
import victorvld.gameofthree.core.services.GameEngine;
import victorvld.gameofthree.core.services.PlayerRegistry;
import victorvld.gameofthree.infrastructure.controller.NotificationSystem;

@Configuration
public class AppConfig {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public AppConfig(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Bean
    public GameEngine gameEventHandler() {
        return new GameEngine(new GameState(), new PlayerRegistry());
    }

    @Bean
    public NotificationSystem notificationSystem() {
        return new NotificationSystem(messagingTemplate);
    }
}
