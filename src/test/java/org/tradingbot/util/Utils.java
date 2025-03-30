package org.tradingbot.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.CLOSE_MARKET;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.OPEN_MARKET;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.tradingbot.common.Candle;
import org.tradingbot.common.Constants;
import org.tradingbot.common.Interval;
import org.tradingbot.common.bot.period.TradingPeriodEnum;
import org.tradingbot.common.math.Decimal;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.stock.RefreshBean;
import org.tradingbot.stock.StockBean;
import org.tradingbot.stock.StockController;
import org.tradingbot.stock.StockCreateBean;
import org.tradingbot.stock.StockRepository;
import org.tradingbot.strategy.StrategyController;
import org.tradingbot.strategy.StrategyCreationBean;
import org.tradingbot.tradingconfig.TradingConfigBean;
import org.tradingbot.tradingconfig.TradingConfigController;
import org.tradingbot.tradingconfig.TradingConfigRepository;

public class Utils {
    public static TradingConfigBean createConfig(TradingConfigRepository tradingConfigRepository, StockRepository stockRepository, StockController stockController,
                                                 StrategyController strategyController,
                                                 TradingConfigController tradingConfigController,
                                                 FinancialProvider fp, String strategyXmlFileName) throws IOException {

        var stockBean = (StockBean) stockController.createStock(new StockCreateBean(fp.getProviderId(), "SW"));
        stockController.refreshCandles(stockBean.getId(), Interval.DAILY, fp.getProviderId(), new RefreshBean(ZonedDateTime.now().minusYears(5), ZonedDateTime.now()));
        String xml = Utils.readFile(Utils.getFile(strategyXmlFileName).toPath());
        var strategyEntity = strategyController.createStrategy(new StrategyCreationBean("RSI", xml, OPEN_MARKET));
        var stockEntity = stockRepository.findById(stockBean.getId()).get();
        tradingConfigRepository.deleteAll();
        return tradingConfigController.createTradingConfig(
                new TradingConfigEntity(strategyEntity, stockEntity, true,
                        ZonedDateTime.now().minusYears(5), Interval.DAILY));
    }

    public static String readFile(Path path) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }

        return contentBuilder.toString();
    }

    public static File getFile(String fileName) {

        URL resource = ClassLoader.getSystemClassLoader().getResource(fileName);
        assertNotNull(resource);
        return new File(resource.getFile());
    }

    public static List<Candle> load(File file) throws IOException {
        List<Candle> candles = new ArrayList<>();
        try (FileReader fr = new FileReader(file); BufferedReader reader = new BufferedReader(fr)) {
            reader.readLine(); // ignore the first line
            String line;
            while ((line = reader.readLine()) != null) {
                var rows = line.split(",");
                var date = ZonedDateTime.of(LocalDate.parse(rows[0]), LocalTime.MIN, Constants.UTC);
                var candle = new Candle.Builder().date(date).open(new Decimal(rows[1])).high(new Decimal(rows[2]))
                        .low(new Decimal(rows[3])).close(new Decimal(rows[4])).adjClose(new Decimal(rows[5]))
                        .volume(Long.valueOf(rows[6])).build();
                candles.add(candle);
            }
        }
        return candles;
    }
}
