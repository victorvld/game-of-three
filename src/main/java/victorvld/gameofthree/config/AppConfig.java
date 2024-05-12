package victorvld.gameofthree.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import victorvld.gameofthree.domain.entities.GameState;
import victorvld.gameofthree.domain.services.GameOfThreeEventHandler;
import victorvld.gameofthree.domain.services.PlayerRegistry;

@Configuration
public class AppConfig {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public AppConfig(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    @Bean
    public GameOfThreeEventHandler gameEventHandler() {
        return new GameOfThreeEventHandler(messagingTemplate, new GameState(), new PlayerRegistry());
    }
}
