package pl.tzawartko.cryptocurrency.converter.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;
import pl.tzawartko.cryptocurrency.converter.Converter;
import pl.tzawartko.cryptocurrency.service.impl.CryptocurrencyUsdPrice;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CryptocurrencyConverter implements Converter {

    private static final String DATA_FIELD = "data";
    private static final String SYMBOL_FIELD = "symbol";
    private static final String QUOTE_FIELD = "quote";
    private static final String USD_FIELD = "USD";
    private static final String PRICE_FIELD = "price";

    public List<CryptocurrencyUsdPrice> fromJsonString(String jsonString) {
        JsonObject cryptocurrencyQuotations = new JsonParser().parse(jsonString).getAsJsonObject();
        JsonArray cryptoCurrencyQuotationsData = cryptocurrencyQuotations.get(DATA_FIELD).getAsJsonArray();
        return StreamSupport.stream(cryptoCurrencyQuotationsData.spliterator(), false)
                .map(currencyQuotationData -> map(currencyQuotationData.getAsJsonObject())).collect(Collectors.toList());
    }

    private CryptocurrencyUsdPrice map(JsonObject currencyQuotationData) {
        String name = currencyQuotationData.get(SYMBOL_FIELD).getAsString();
        JsonObject quote = currencyQuotationData.get(QUOTE_FIELD).getAsJsonObject();
        JsonObject usdQuote = quote.get(USD_FIELD).getAsJsonObject();
        Double usdPrice = usdQuote.get(PRICE_FIELD).getAsDouble();
        return new CryptocurrencyUsdPrice(name, usdPrice);
    }
}
