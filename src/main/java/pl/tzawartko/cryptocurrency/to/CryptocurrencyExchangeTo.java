package pl.tzawartko.cryptocurrency.to;

import java.util.Map;

public class CryptocurrencyExchangeTo {

    private final String source;
    private final Map<String, Double> rates;

    public CryptocurrencyExchangeTo(String source, Map<String, Double> rates) {
        this.source = source;
        this.rates = rates;
    }

    public String getSource() {
        return source;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}
