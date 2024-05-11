package victorvld.gameofthree.controller;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import victorvld.gameofthree.controller.dto.GameMessage;
import victorvld.gameofthree.controller.exceptions.PlayerConnectionException;
import victorvld.gameofthree.controller.exceptions.StartGameException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public GameMessage handlePlayerConnectionException(PlayerConnectionException e) {
        return new GameMessage(e.getMessage());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public GameMessage handleStartGameException(StartGameException e) {
        return new GameMessage(e.getMessage());
    }
}
