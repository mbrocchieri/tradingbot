package org.tradingbot.util;

import org.tradingbot.common.Interval;
import org.tradingbot.common.provider.ProvidersSingleton;
import org.tradingbot.common.provider.YahooAPI;
import org.tradingbot.common.provider.YahooProvider;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;

import static org.tradingbot.common.Constants.UTC;

public class ProviderCandlesToFile {
    public static void main(String[] args) throws IOException {
        var from = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, UTC);
        var to = ZonedDateTime.of(2022, 8, 19, 0, 0, 0, 0, UTC);
        var params = YahooAPI.createParams(from, to, Interval.DAILY);
        var inputStream = YahooAPI.getCandlesInputStream("CGG.PA", params);
        try (var is = new InputStreamReader(inputStream); var br = new BufferedReader(is);
             var writer = new FileWriter("/tmp/file.csv")) {
            String line;
            while ((line = br.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
        }
    }
}
