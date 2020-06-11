package pl.tzawartko.cryptocurrency.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "config.json",
        factory = JsonPropertySourceConfigFactory.class)
@ConfigurationProperties
public class CryptocurrencyConfiguration {
    private String apiKey;
    private String apiUri;
    private Double feePercent;

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUri() {
        return apiUri;
    }

    public Double getFeePercent() {
        return feePercent;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }

    public void setFeePercent(Double feePercent) {
        this.feePercent = feePercent;
    }
}
