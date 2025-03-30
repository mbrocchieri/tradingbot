package org.tradingbot.common;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SymbolData {

    public static List<String> getSBF120() throws IOException {
        var url = new URL("https://query2.finance.yahoo.com/v10/finance/quoteSummary/%5ESBF120?formatted=true&crumb=2s0xWzNBlfu&lang=fr-FR&region=FR&modules=components&corsDomain=fr.finance.yahoo.com");
        List<String> symbols = new ArrayList<>();
        try (var scanner = new Scanner(url.openStream())) {
            String response = scanner.useDelimiter("\\Z").next();
            var jsonObject = new JSONObject(response);
            jsonObject.getJSONObject("quoteSummary")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("components")
                    .getJSONArray("components").forEach(e -> symbols.add(e.toString()));
        }
        symbols.remove("UG.PA");
        symbols.remove("ING.PA");
        symbols.remove("FP.PA");
        return symbols;
    }

}
