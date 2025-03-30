package org.tradingbot.common.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.math.Decimal;

public class RestProvider implements FinancialProvider {

    @Override
    public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval)
            throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // HH:mm:ss Z

        URL url = new URL(System.getenv("STOCK_URL") + "/stock-history/" + symbol + "/" + from.format(formatter) + "/"
                + to.format(formatter));
        URLConnection connection = url.openConnection();
        var candles = new ArrayList<Candle>();
        try (var is = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            is.readLine(); // ignore header
            String line;
            while ((line = is.readLine()) != null) {
                var cells = StringUtils.split(line, ",");
                var dt = ZonedDateTime.parse(cells[0].replace(" ", "T"));
                candles.add(
                        new Candle.Builder().date(dt)
                                .open(new Decimal(cells[1]))
                                .high(new Decimal(cells[2]))
                                .low(new Decimal(cells[3]))
                                .close(new Decimal(cells[4]))
                                .adjClose(new Decimal(cells[4]))
                                .volume(Long.parseLong(cells[5]))
                                .build());
            }

        }
        return candles;
    }

    @Override
    public StockData getStockData(String providerCode) throws IOException {
        URL url = new URL(System.getenv("STOCK_URL") + "/stock/" + providerCode);
        URLConnection connection = url.openConnection();
        try (var is = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            String response = "";
            while ((line = is.readLine()) != null) {
                response += line;
            }
            JSONObject jsonObject = new JSONObject(response);
            var marketName = jsonObject.getString("exchange");
            Integer marketId = YahooProvider.marketCodeToId.get(marketName);
            if (marketId == null) {
                throw new IOException("Do not know market name " + marketName);
            }
            return new StockData(jsonObject.getString("currency"), jsonObject.getString("shortName"), providerCode,
                    marketId);
        }
    }

    @Override
    public int getProviderId() {
        return 1;
    }

}
