package org.tradingbot.common.chat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.tradingbot.alert.WebSocketHandler;

public class WebServerClient extends ChatClient {

    private final WebSocketHandler webSocket;

    public WebServerClient(WebSocketHandler webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public void sendMessage(String message) {
        webSocket.sendMessage(new TextMessage("{\"alert\":\"" + StringUtils.replace(message, "\"", "\\\"") + "\"}"));
    }
}
