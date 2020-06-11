package pl.tzawartko.cryptocurrency.exception;

public class CryptocurrencyNotFoundException extends RuntimeException {

    public CryptocurrencyNotFoundException() {
        super("Cryptocurrency not found.");
    }

    public CryptocurrencyNotFoundException(String message) {
        super(message);
    }
}
