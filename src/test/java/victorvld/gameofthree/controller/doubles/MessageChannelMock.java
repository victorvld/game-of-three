package victorvld.gameofthree.controller.doubles;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.AbstractSubscribableChannel;

import java.util.ArrayList;
import java.util.List;

public class MessageChannelMock extends AbstractSubscribableChannel {

    private final List<Message<?>> messages = new ArrayList<>();


    public List<Message<?>> getMessages() {
        return this.messages;
    }

    @Override
    protected boolean sendInternal(Message<?> message, long timeout) {
        this.messages.add(message);
        return true;
    }

}
