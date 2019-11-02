package pl.dmarciniak.jsonpathmapper.exception;

/**
 * Json field value validation exception
 */
public class JsonPathMapperException extends RuntimeException {
    public JsonPathMapperException(String message) {
        super(message);
    }

    public JsonPathMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
