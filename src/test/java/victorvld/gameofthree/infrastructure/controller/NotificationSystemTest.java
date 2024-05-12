package victorvld.gameofthree.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Queue;

import org.mockito.Mockito;
import victorvld.gameofthree.infrastructure.controller.dto.GameMessage;

import java.util.LinkedList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class NotificationSystemTest {

    private final SimpMessagingTemplate messagingTemplateMock = Mockito.mock(SimpMessagingTemplate.class);
    private final NotificationSystem underTest = new NotificationSystem(messagingTemplateMock);

    @Test
    void testNotifySingleMessage() {

        String testMessage = "Test message";

        underTest.notify(testMessage);

        verify(messagingTemplateMock).convertAndSend("/topic/messages", new GameMessage(testMessage));
    }

    @Test
    void testNotifyMultipleMessages() {
        Queue<String> testMessages = new LinkedList<>();
        testMessages.add("Test message 1");
        testMessages.add("Test message 2");

        underTest.notify(testMessages);

        ArgumentCaptor<GameMessage> gameMessageCaptor = ArgumentCaptor.forClass(GameMessage.class);
        verify(messagingTemplateMock, Mockito.times(2)).convertAndSend(any(String.class), gameMessageCaptor.capture());

        for (String message : testMessages) {
            verify(messagingTemplateMock).convertAndSend("/topic/messages", new GameMessage(message));
        }
    }
}
