package org.tradingbot.common.bot.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.num.Num;
import org.tradingbot.common.chat.ChatClient;
import org.tradingbot.common.math.Decimal;

import java.util.Objects;

public class SendChatAction implements TradingAction {

    private static final Logger LOG = LoggerFactory.getLogger(SendChatAction.class);

    private final ChatClient chatClient;

    public SendChatAction(ChatClient chatClient) {
        this.chatClient = Objects.requireNonNull(chatClient);
    }

    @Override
    public void exit(String strategyName, String symbol, Num closePrice) {
        sendTradingMessage(strategyName, "Sell ", Objects.requireNonNull(symbol), Objects.requireNonNull(closePrice));
    }

    @Override
    public void enter(String strategyName, String symbol, Num closePrice) {
        sendTradingMessage(strategyName, "Buy ", Objects.requireNonNull(symbol), Objects.requireNonNull(closePrice));
    }

    @Override
    public void sendStockCandlesCompareError(String symbol) {
        sendMessage("Difference detected between provider and database, all about " + symbol + " is ignored");
    }

    @Override
    public void sendConfigError(int id) {
        sendMessage("Error with config " + id);
    }

    private void sendTradingMessage(String strategyName, String action, String symbol, Num closePrice) {
        LOG.debug("send message to discord");
        String s = Decimal.getPrintablePrice(closePrice);
        sendMessage(strategyName + " " + action + symbol + " price : " + s);
    }

    private void sendMessage(String message) {
        chatClient.sendMessage(message);
    }
}
