package pl.tzawartko.cryptocurrency.body;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;


public class CryptocurrencyExchangeResponseBody implements Serializable {

    @JsonProperty("from")
    private String fromCurrencyName;

    @JsonProperty("exchange")
    private Map<String, ExchangeForecast> currencyToExchangeForecast;

    public static class ExchangeForecast implements Serializable {
        Map<ExchangeParameters, Double> exchangeForecastResult;

        public void setExchangeForecastResult(Map<ExchangeParameters, Double> exchangeForecastResult) {
            this.exchangeForecastResult = exchangeForecastResult;
        }

        public Map<ExchangeParameters, Double> getExchangeForecastResult() {
            return exchangeForecastResult;
        }

    }

    public enum ExchangeParameters implements Serializable {

        @JsonProperty("rate")
        RATE(),
        @JsonProperty("amount")
        AMOUNT(),
        @JsonProperty("result")
        RESULT(),
        @JsonProperty("fee")
        FEE();
    }

    public String getFromCurrencyName() {
        return fromCurrencyName;
    }

    public Map<String, ExchangeForecast> getCurrencyToExchangeForecast() {
        return currencyToExchangeForecast;
    }

    public void setFromCurrencyName(String fromCurrencyName) {
        this.fromCurrencyName = fromCurrencyName;
    }

    public void setCurrencyToExchangeForecast(Map<String, ExchangeForecast> currencyToExchangeForecast) {
        this.currencyToExchangeForecast = currencyToExchangeForecast;
    }
}
