package victorvld.gameofthree.controller;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import victorvld.gameofthree.controller.dto.Message;
import victorvld.gameofthree.controller.exceptions.PlayerConnectionException;
import victorvld.gameofthree.controller.exceptions.StartGameException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public Message handlePlayerConnectionException(PlayerConnectionException e) {
        return new Message(e.getMessage());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public Message handleStartGameException(StartGameException e) {
        return new Message(e.getMessage());
    }
}
