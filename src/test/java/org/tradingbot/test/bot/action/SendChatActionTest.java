package org.tradingbot.test.bot.action;

import org.junit.jupiter.api.Test;
import org.ta4j.core.num.DecimalNum;
import org.tradingbot.common.bot.action.SendChatAction;
import org.tradingbot.common.chat.ChatClient;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SendChatActionTest {

    @Test
    public void testSendChat() {
        AtomicReference<String> s = new AtomicReference<>();
        SendChatAction sendChatAction = new SendChatAction(new ChatClient() {
            @Override
            public void sendMessage(String message) {
                s.set(message);
            }

            @Override
            public void close() {
                // do nothing
            }
        });

        sendChatAction.enter("ab", "c", DecimalNum.valueOf(1));
        assertEquals("ab Buy c price : 1", s.get());

        sendChatAction.enter("ab", "c", DecimalNum.valueOf("1.23456789"));
        assertEquals("ab Buy c price : 1.2346", s.get());

        sendChatAction.enter("ab", "c", DecimalNum.valueOf("12.3456789"));
        assertEquals("ab Buy c price : 12.346", s.get());

        sendChatAction.enter("ab", "c", DecimalNum.valueOf("123.456789"));
        assertEquals("ab Buy c price : 123.46", s.get());

        sendChatAction.enter("ab", "c", DecimalNum.valueOf("1234.56789"));
        assertEquals("ab Buy c price : 1234.6", s.get());

        sendChatAction.enter("ab", "c", DecimalNum.valueOf("12345.6789"));
        assertEquals("ab Buy c price : 12346", s.get());


        sendChatAction.enter("ab", "c", DecimalNum.valueOf("123456.789"));
        assertEquals("ab Buy c price : 123456", s.get());

        sendChatAction.enter("ab", "c", DecimalNum.valueOf("1234567.89"));
        assertEquals("ab Buy c price : 1234567", s.get());

        sendChatAction.exit("de", "f", DecimalNum.valueOf(2));
        assertEquals("de Sell f price : 2", s.get());
    }
}