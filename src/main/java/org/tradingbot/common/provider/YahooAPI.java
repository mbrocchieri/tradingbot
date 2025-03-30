package org.tradingbot.common.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tradingbot.common.Candle;
import org.tradingbot.common.Constants;
import org.tradingbot.common.Interval;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class YahooAPI {
    private static final Logger LOG = LoggerFactory.getLogger(YahooAPI.class);

    private static String getURLParameters(Map<String, String> params) {
        var sb = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            String key = entry.getKey();
            String value = entry.getValue();
            key = URLEncoder.encode(key, StandardCharsets.UTF_8);
            value = URLEncoder.encode(value, StandardCharsets.UTF_8);
            sb.append(String.format("%s=%s", key, value));
        }
        return sb.toString();
    }

    public static Map<String, String> createParams(ZonedDateTime from, ZonedDateTime to, Interval interval) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("period1", String.valueOf(from.toInstant().toEpochMilli() / 1000));
        params.put("period2", String.valueOf(to.toInstant().toEpochMilli() / 1000));

        String intervalString;
        switch (interval) {
            case DAILY:
                intervalString = "1d";
                break;
            case WEEKLY:
                intervalString = "5d";
                throw new IllegalStateException("Not implemented for " + interval);
            case MONTHLY:
                intervalString = "1mo";
                throw new IllegalStateException("Not implemented for " + interval);
            default:
                throw new IllegalStateException("Not implemented for " + interval);
        }
        params.put("interval", intervalString);
        return params;
    }

    /**
     * @param symbol Yahoo stock identify
     * @param params Yahoo parameters
     * @param from   period from
     * @param to     period to
     * @return if not found, returns an empty list
     * @throws IOException if error getting data from Yahoo
     */
    static List<Candle> getCandles(String symbol, Map<String, String> params, ZonedDateTime from, ZonedDateTime to)
            throws IOException {

        InputStream inputStream = getCandlesInputStream(symbol, params);
        List<Candle> candles = new ArrayList<>();
        try (var is = new InputStreamReader(inputStream); var br = new BufferedReader(is)) {
            String json = br.readLine();
            LOG.debug("yahoo header = {}", json);// skip the first line
            // Parse CSV
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode resultNode = objectMapper.readTree(json).get("chart").get("result").get(0);
            JsonNode timestamps = resultNode.get("timestamp");
            JsonNode indicators = resultNode.get("indicators");
            JsonNode quotes = indicators.get("quote").get(0);
            JsonNode closes = quotes.get("close");
            JsonNode volumes = quotes.get("volume");
            JsonNode opens = quotes.get("open");
            JsonNode highs = quotes.get("high");
            JsonNode lows = quotes.get("low");
            JsonNode adjCloses = indicators.get("adjclose").get(0).get("adjclose");

            for (int i = 0; i < timestamps.size(); i++) {
                long timestamp = timestamps.get(i).asLong();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp * 1000);
                BigDecimal adjClose = adjCloses.get(i).decimalValue();
                long volume = volumes.get(i).asLong();
                BigDecimal open = opens.get(i).decimalValue();
                BigDecimal high = highs.get(i).decimalValue();
                BigDecimal low = lows.get(i).decimalValue();
                BigDecimal close = closes.get(i).decimalValue();

                TimeZone tz = calendar.getTimeZone();
                ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
                var localDate = LocalDate.ofInstant(calendar.toInstant(), zid);

                var date = ZonedDateTime.of(localDate, LocalTime.MIN, Constants.UTC);

                if (date.compareTo(from) >= 0 && date.compareTo(to) <= 0) {
                    candles.add(new Candle.Builder().date(date).open(open).high(high).low(low).close(close)
                            .adjClose(adjClose).volume(volume).build());
                }
            }
        }
        return candles;
    }

    public static StockData getStockData(String providerCode, Map<String, Integer> marketCodeToId) throws IOException {
        String url = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=" +
                URLEncoder.encode(providerCode, StandardCharsets.UTF_8);
        var request = new URL(url);
        var redirectableRequest = new RedirectableRequest(request, 5);
        redirectableRequest.setConnectTimeout(10000);
        redirectableRequest.setReadTimeout(10000);
        URLConnection connection = redirectableRequest.openConnection();
        try (var is = new InputStreamReader(connection.getInputStream())) {
            var jsonElement = JsonParser.parseReader(is);
            var array = jsonElement.getAsJsonObject().get("quoteResponse").getAsJsonObject().get("result")
                    .getAsJsonArray();
            if (array.size() == 0) {
                throw new IOException("Missing data for symbol " + providerCode + " for provider Yahoo");
            }
            final var jsonObject = array.get(0).getAsJsonObject();
            var currency = jsonObject.get("currency").getAsString();
            var name = jsonObject.get("shortName").getAsString();
            var marketName = jsonObject.get("fullExchangeName").getAsString();
            String mnemonic;
            Integer marketId = marketCodeToId.get(marketName);
            if (marketId == null) {
                throw new IOException("Do not know market name " + marketName);
            }

            if (providerCode.contains(".")) {
                final var split = StringUtils.split(providerCode, ".");
                mnemonic = split[0];
            } else {
                mnemonic = providerCode;
            }

            return new StockData(currency, name, mnemonic, marketId);
        }
    }

    public static InputStream getCandlesInputStream(String symbol, Map<String, String> params)
            throws IOException {
        String url = "https://query2.finance.yahoo.com/v8/finance/chart/" +
                URLEncoder.encode(symbol, StandardCharsets.UTF_8) + "?" + getURLParameters(params);

        var request = new URL(url);
        var redirectableRequest = new RedirectableRequest(request, 5);
        redirectableRequest.setConnectTimeout(10000);
        redirectableRequest.setReadTimeout(10000);
        URLConnection connection = redirectableRequest.openConnection();
        return connection.getInputStream();
    }

    public static List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval)
            throws IOException {
        var params = createParams(from, to, interval);
        return getCandles(symbol, params, from, to);
    }
}
