package pl.tzawartko.cryptocurrency.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.tzawartko.cryptocurrency.api.CoinApi;
import pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeRequestBody;
import pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeResponseBody;
import pl.tzawartko.cryptocurrency.config.CryptocurrencyConfiguration;
import pl.tzawartko.cryptocurrency.converter.Converter;
import pl.tzawartko.cryptocurrency.exception.CryptocurrencyNotFoundException;
import pl.tzawartko.cryptocurrency.service.CurrencyService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeResponseBody.ExchangeForecast;
import static pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeResponseBody.ExchangeParameters;

@Service
public class CryptocurrencyService implements CurrencyService {

    @Autowired
    CoinApi coinApi;

    @Autowired
    Converter converter;

    @Autowired
    CryptocurrencyConfiguration configuration;

    @Override
    public Map<String, Double> currencyQuotations(String currency, List<String> filters) throws IOException, URISyntaxException {
        return getRatesFromApiCall(currency, filters);
    }

    @Override
    public CryptocurrencyExchangeResponseBody currencyExchangeForecast(CryptocurrencyExchangeRequestBody body) throws IOException, URISyntaxException {
        String fromCryptocurrency = body.getFromCurrency();
        List<String> toCryptocurrency = body.getToCurrency();
        Double amount = body.getAmount();
        Map<String, Double> rates = getRatesFromApiCall(fromCryptocurrency, toCryptocurrency);
        CryptocurrencyExchangeResponseBody responseBody = new CryptocurrencyExchangeResponseBody();
        responseBody.setFromCurrencyName(fromCryptocurrency);
        Map<String, ExchangeForecast> currencyToExchangeForecast = toCryptocurrency.stream()
                .filter(rates::containsKey)
                .collect(Collectors.toMap(currencyName -> currencyName,
                        currencyName -> computeForecast(amount, rates.get(currencyName))));
        responseBody.setCurrencyToExchangeForecast(currencyToExchangeForecast);
        return responseBody;

    }

    private ExchangeForecast computeForecast(Double amount, Double rate) {
        ExchangeForecast exchangeForecast = new ExchangeForecast();
        Map<ExchangeParameters, Double> exchangeForecastResult = new HashMap<>();
        exchangeForecastResult.put(ExchangeParameters.RATE, rate);
        exchangeForecastResult.put(ExchangeParameters.AMOUNT, amount);
        exchangeForecastResult.put(ExchangeParameters.RESULT, amount * rate);
        exchangeForecastResult.put(ExchangeParameters.FEE, amount * configuration.getFeePercent());
        exchangeForecast.setExchangeForecastResult(exchangeForecastResult);
        return exchangeForecast;
    }

    private Map<String, Double> getRatesFromApiCall(String currency, List<String> filters) throws URISyntaxException, IOException {

        String responseContent = coinApi.call();
        List<CryptocurrencyUsdPrice> prices = converter.fromJsonString(responseContent);

        CryptocurrencyUsdPrice fromCurrency = prices.stream()
                .filter(currencyPrice -> currency.equalsIgnoreCase(currencyPrice.getCryptocurrencySymbol()))
                .findAny()
                .orElseThrow(CryptocurrencyNotFoundException::new);

        return prices.stream()
                .filter(cryptocurrencyUsdPrice -> !cryptocurrencyUsdPrice.getCryptocurrencySymbol().equalsIgnoreCase(fromCurrency.getCryptocurrencySymbol()))
                .filter(cryptocurrencyUsdPrice -> shouldBeFilterOut(cryptocurrencyUsdPrice.getCryptocurrencySymbol(), filters))
                .collect(Collectors.
                        toMap(CryptocurrencyUsdPrice::getCryptocurrencySymbol,
                                cryptocurrencyUsdPrice -> computeRate(fromCurrency, cryptocurrencyUsdPrice)));
    }

    private Double computeRate(CryptocurrencyUsdPrice fromCurrency, CryptocurrencyUsdPrice toCurrency) {
        return fromCurrency.getUsdPrice() / toCurrency.getUsdPrice();

    }

    private boolean shouldBeFilterOut(String cryptocurrencySymbol, List<String> filters) {
        if (filters != null && !filters.isEmpty()) {
            return filters.stream().anyMatch(cryptocurrencySymbol::equalsIgnoreCase);
        } else {
            return true;
        }
    }
}
