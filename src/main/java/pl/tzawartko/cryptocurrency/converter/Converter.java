package pl.tzawartko.cryptocurrency.converter;

import pl.tzawartko.cryptocurrency.service.impl.CryptocurrencyUsdPrice;

import java.util.List;

public interface Converter {
    List<CryptocurrencyUsdPrice> fromJsonString(String jsonString);
}
