package pl.tzawartko.cryptocurrency.api;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.tzawartko.cryptocurrency.config.CryptocurrencyConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class CoinMarketCapApi implements CoinApi {

    @Autowired
    CryptocurrencyConfiguration configuration;

    @Override
    public String call() throws URISyntaxException, IOException {
        URIBuilder query = new URIBuilder(configuration.getApiUri());
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", configuration.getApiKey());
        String responseContent ="";
        try(CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
        }
        return responseContent;
    }
}
