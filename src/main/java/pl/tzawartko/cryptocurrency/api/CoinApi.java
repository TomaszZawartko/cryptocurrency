package pl.tzawartko.cryptocurrency.api;

import java.io.IOException;
import java.net.URISyntaxException;

public interface CoinApi {

    String call() throws URISyntaxException, IOException;
}
