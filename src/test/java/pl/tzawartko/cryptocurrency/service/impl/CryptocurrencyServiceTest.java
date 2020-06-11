package pl.tzawartko.cryptocurrency.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.tzawartko.cryptocurrency.api.CoinApi;
import pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeRequestBody;
import pl.tzawartko.cryptocurrency.body.CryptocurrencyExchangeResponseBody;
import pl.tzawartko.cryptocurrency.config.CryptocurrencyConfiguration;
import pl.tzawartko.cryptocurrency.converter.impl.CryptocurrencyConverter;
import pl.tzawartko.cryptocurrency.exception.CryptocurrencyNotFoundException;
import pl.tzawartko.cryptocurrency.mock.CryptocurrencyMock;
import pl.tzawartko.cryptocurrency.service.CurrencyService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CryptocurrencyService.class, CryptocurrencyConverter.class})
public class CryptocurrencyServiceTest {

    @MockBean
    CoinApi coinApi;

    @MockBean
    CryptocurrencyExchangeRequestBody requestBody;

    @MockBean
    CryptocurrencyConfiguration configuration;

    @Autowired
    CurrencyService cryptocurrencyService;

    private static final double BTC_ETH_RATE = 39.67573246133493;
    private static final double BTC_USDT_RATE = 9650.2377956866;

    private static final String BTC_SYMBOL = "BTC";
    private static final String ETH_SYMBOL = "ETH";
    private static final String USDT_SYMBOL = "USDT";
    private static final String KNC_SYMBOL = "KNC";
    private static final String XXX_SYMBOL = "XXX";
    private static final String YYY_SYMBOL = "YYY";


    @Test
    public void shouldFilterOutCurrency() throws IOException, URISyntaxException {
        //given
        Mockito.when(coinApi.call()).thenReturn(CryptocurrencyMock.BTC_ETH_MOCK);

        //when
        Map<String, Double> response = cryptocurrencyService.currencyQuotations(BTC_SYMBOL, asList(ETH_SYMBOL, KNC_SYMBOL));

        //then
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(ETH_SYMBOL)).isEqualTo(BTC_ETH_RATE);
    }

    @Test
    public void shouldNotFilterOutWhenFilterIsEmpty() throws IOException, URISyntaxException {
        //given
        Mockito.when(coinApi.call()).thenReturn(CryptocurrencyMock.BTC_ETH_USDT_MOCK);

        //when
        Map<String, Double> response = cryptocurrencyService.currencyQuotations(BTC_SYMBOL, Collections.emptyList());

        //then
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(ETH_SYMBOL)).isEqualTo(BTC_ETH_RATE);
        assertThat(response.get(USDT_SYMBOL)).isEqualTo(BTC_USDT_RATE);

    }

    @Test
    public void shouldNotFilterOutWhenFilterIsNull() throws IOException, URISyntaxException {
        //given
        Mockito.when(coinApi.call()).thenReturn(CryptocurrencyMock.BTC_ETH_USDT_MOCK);

        //when
        Map<String, Double> response = cryptocurrencyService.currencyQuotations(BTC_SYMBOL, null);

        //then
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(ETH_SYMBOL)).isEqualTo(BTC_ETH_RATE);
        assertThat(response.get(USDT_SYMBOL)).isEqualTo(BTC_USDT_RATE);
    }

    @Test
    public void shouldETHFilterOut() throws IOException, URISyntaxException {
        //given
        Mockito.when(coinApi.call()).thenReturn(CryptocurrencyMock.BTC_ETH_USDT_MOCK);

        //when
        Map<String, Double> response = cryptocurrencyService.currencyQuotations(BTC_SYMBOL, asList(USDT_SYMBOL, KNC_SYMBOL));

        //then
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(ETH_SYMBOL)).isNull();
        assertThat(response.get(USDT_SYMBOL)).isEqualTo(BTC_USDT_RATE);

    }

    @Test(expected = CryptocurrencyNotFoundException.class)
    public void shouldCryptocurrencyNotFoundExceptionThrow() throws IOException, URISyntaxException {
        //given
        Mockito.when(coinApi.call()).thenReturn(CryptocurrencyMock.BTC_ETH_USDT_MOCK);

        //when
        cryptocurrencyService.currencyQuotations(XXX_SYMBOL, Collections.emptyList());
    }

    @Test
    public void shouldEmptyResultWhenCurrenciesNotCorrect() throws IOException, URISyntaxException {
        //given
        Mockito.when(coinApi.call()).thenReturn(CryptocurrencyMock.BTC_ETH_USDT_MOCK);

        //when
        Map<String, Double> response = cryptocurrencyService.currencyQuotations(BTC_SYMBOL, asList(XXX_SYMBOL, YYY_SYMBOL));

        //then
        assertThat(response.size()).isEqualTo(0);
    }

    @Test
    public void testCurrencyExchange() throws IOException, URISyntaxException {
        //given
        Mockito.when(requestBody.getFromCurrency()).thenReturn(BTC_SYMBOL);
        Mockito.when(requestBody.getToCurrency()).thenReturn(asList(USDT_SYMBOL, ETH_SYMBOL));
        Mockito.when(requestBody.getAmount()).thenReturn(120.0);
        Mockito.when(configuration.getFeePercent()).thenReturn(0.01);

        Mockito.when(coinApi.call()).thenReturn(CryptocurrencyMock.BTC_ETH_USDT_MOCK);

        //when
        CryptocurrencyExchangeResponseBody response = cryptocurrencyService.currencyExchangeForecast(requestBody);

        //then
        assertThat(response.getFromCurrencyName()).isEqualTo(BTC_SYMBOL);

        assertThat(response.getCurrencyToExchangeForecast().get(USDT_SYMBOL).getExchangeForecastResult().
                get(CryptocurrencyExchangeResponseBody.ExchangeParameters.FEE)).isEqualTo(1.2);
        assertThat(response.getCurrencyToExchangeForecast().get(USDT_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.AMOUNT)).isEqualTo(120.0);
        assertThat(response.getCurrencyToExchangeForecast().get(USDT_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.RATE)).isEqualTo(BTC_USDT_RATE);
        assertThat(response.getCurrencyToExchangeForecast().get(USDT_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.RESULT)).isEqualTo(1158028.535482392);

        assertThat(response.getCurrencyToExchangeForecast().get(ETH_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.FEE)).isEqualTo(1.2);
        assertThat(response.getCurrencyToExchangeForecast().get(ETH_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.AMOUNT)).isEqualTo(120.0);
        assertThat(response.getCurrencyToExchangeForecast().get(ETH_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.RATE)).isEqualTo(BTC_ETH_RATE);
        assertThat(response.getCurrencyToExchangeForecast().get(ETH_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.RESULT)).isEqualTo(4761.087895360191);
    }

    @Test
    public void shouldIgnoreWrongCurrencyNameInExchangeForecast() throws IOException, URISyntaxException {
        //given
        Mockito.when(requestBody.getFromCurrency()).thenReturn(BTC_SYMBOL);
        Mockito.when(requestBody.getToCurrency()).thenReturn(asList(USDT_SYMBOL, XXX_SYMBOL));
        Mockito.when(requestBody.getAmount()).thenReturn(120.0);
        Mockito.when(configuration.getFeePercent()).thenReturn(0.01);

        Mockito.when(coinApi.call()).thenReturn(CryptocurrencyMock.BTC_ETH_USDT_MOCK);

        //when
        CryptocurrencyExchangeResponseBody response = cryptocurrencyService.currencyExchangeForecast(requestBody);

        //then
        assertThat(response.getFromCurrencyName()).isEqualTo(BTC_SYMBOL);

        assertThat(response.getCurrencyToExchangeForecast().get(USDT_SYMBOL).getExchangeForecastResult().
                get(CryptocurrencyExchangeResponseBody.ExchangeParameters.FEE)).isEqualTo(1.2);
        assertThat(response.getCurrencyToExchangeForecast().get(USDT_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.AMOUNT)).isEqualTo(120.0);
        assertThat(response.getCurrencyToExchangeForecast().get(USDT_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.RATE)).isEqualTo(BTC_USDT_RATE);
        assertThat(response.getCurrencyToExchangeForecast().get(USDT_SYMBOL).getExchangeForecastResult()
                .get(CryptocurrencyExchangeResponseBody.ExchangeParameters.RESULT)).isEqualTo(1158028.535482392);

        assertThat(response.getCurrencyToExchangeForecast().get(XXX_SYMBOL)).isNull();
    }
}
