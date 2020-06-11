package pl.tzawartko.cryptocurrency.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DefaultResponseStatusException {

    public static void throwNotAcceptableParameterException(String message, Exception ex) {
        throw new ResponseStatusException(
                HttpStatus.NOT_ACCEPTABLE,
                message,
                ex);
    }

    public static void throwInternalServerError(String message, Exception ex) {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                message,
                ex);
    }
}
