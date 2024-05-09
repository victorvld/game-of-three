package victorvld.gameofthree.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import victorvld.gameofthree.controller.dto.GameBoard;
import victorvld.gameofthree.controller.dto.Message;
import victorvld.gameofthree.controller.dto.StartGameEvent;
import victorvld.gameofthree.domain.services.GameOfThreeEventHandler;

@Controller
public class GameOfThreeController {

    private final Logger logger = LoggerFactory.getLogger(GameOfThreeController.class);
    private final GameOfThreeEventHandler eventHandler;
    @Autowired
    public GameOfThreeController(GameOfThreeEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @MessageMapping("/start")
    @SendTo("/topic/game")
    public GameBoard start(StartGameEvent event) {
        return eventHandler.handleStartGameEvent(event);
    }
    @MessageMapping("/connect/{playerId}")
    @SendTo("/topic/messages")
    public Message start(@DestinationVariable String playerId) {
        return eventHandler.handleConnectEvent(playerId);
    }


}
