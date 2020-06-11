package pl.tzawartko.cryptocurrency.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeRequestBody;
import pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeResponseBody;
import pl.tzawartko.cryptocurrency.exception.CryptocurrencyNotFoundException;
import pl.tzawartko.cryptocurrency.exception.DefaultResponseStatusException;
import pl.tzawartko.cryptocurrency.service.CurrencyService;
import pl.tzawartko.cryptocurrency.to.CryptocurrencyExchangeTo;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/currencies")
public class CryptocurrencyRestController {

    @Autowired
    CurrencyService currencyService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CryptocurrencyExchangeTo listOfRatesQuotationsForCurrency(@PathVariable String currency, @RequestParam(name = "filter", required = false) List<String> filterParameters) {
        Map<String, Double> rates = new HashMap<>();
        try {
            rates = currencyService.currencyQuotations(currency.toUpperCase(), filterParameters);
        } catch (CryptocurrencyNotFoundException e) {
            DefaultResponseStatusException.throwNotAcceptableParameterException("Cryptocurrency not found", e);
        } catch (IOException e) {
            DefaultResponseStatusException.throwInternalServerError("Problem with calling coin API.", e);
        } catch (URISyntaxException e) {
            DefaultResponseStatusException.throwInternalServerError("Something wrong with URI to coin API", e);
        }
        return new CryptocurrencyExchangeTo(currency, rates);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/exchange", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CryptocurrencyExchangeResponseBody cryptoCurrencyExchangeForecast(@Valid @RequestBody CryptocurrencyExchangeRequestBody body) {
        try {
            return currencyService.currencyExchangeForecast(body);
        } catch (IOException e) {
            DefaultResponseStatusException.throwInternalServerError("Problem with calling coin API.", e);
        } catch (URISyntaxException e) {
            DefaultResponseStatusException.throwInternalServerError("Something wrong with URI to coin API", e);

        }
        return null;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
