package victorvld.gameofthree.infrastructure.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import victorvld.gameofthree.infrastructure.controller.dto.GameMessage;

import java.util.Queue;

public class NotificationSystem {
    private final Logger logger = LoggerFactory.getLogger(NotificationSystem.class);
    private static final String TOPIC_MESSAGES_ROUTE = "/topic/messages";
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationSystem(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notify(String message) {
        this.logger.info(message);
        this.messagingTemplate.convertAndSend(TOPIC_MESSAGES_ROUTE, new GameMessage(message));
    }

    public void notify(Queue<String> messages) {
        messages.forEach(this::notify);
    }
}
