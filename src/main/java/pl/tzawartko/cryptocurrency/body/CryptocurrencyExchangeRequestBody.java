package pl.tzawartko.cryptocurrency.body;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class CryptocurrencyExchangeRequestBody {

    @JsonProperty("from")
    @NotNull
    @Size(min = 3, max = 10)
    @Pattern(regexp = "[a-zA-Z]+")
    String fromCurrency;

    @JsonProperty("to")
    @NotEmpty
    List<String> toCurrency;

    @NotNull
    Double amount;

    public CryptocurrencyExchangeRequestBody(String from, List<String> to, Double amount) {
        this.fromCurrency = from;
        this.toCurrency = to;
        this.amount = amount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public List<String> getToCurrency() {
        return toCurrency;
    }

    public Double getAmount() {
        return amount;
    }
}
