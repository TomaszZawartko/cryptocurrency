package pl.tzawartko.cryptocurrency.service.impl;

public class CryptocurrencyUsdPrice {

    private final String cryptocurrencySymbol;
    private final Double usdPrice;

    public CryptocurrencyUsdPrice(String cryptocurrencySymbol, Double usdPrice) {
        this.cryptocurrencySymbol = cryptocurrencySymbol;
        this.usdPrice = usdPrice;
    }

    public String getCryptocurrencySymbol() {
        return cryptocurrencySymbol;
    }

    public Double getUsdPrice() {
        return usdPrice;
    }
}
