package pl.tzawartko.cryptocurrency.service;

import pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeRequestBody;
import pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeResponseBody;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface CurrencyService {

    Map<String, Double> currencyQuotations(String currency, List<String> filters) throws IOException, URISyntaxException;

    CryptocurrencyExchangeResponseBody currencyExchangeForecast(CryptocurrencyExchangeRequestBody body) throws IOException, URISyntaxException;
}
