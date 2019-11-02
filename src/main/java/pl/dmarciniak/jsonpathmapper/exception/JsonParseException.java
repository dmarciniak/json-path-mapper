package pl.dmarciniak.jsonpathmapper.exception;

/**
 * Wrong json format
 */
public class JsonParseException extends JsonPathMapperException {
    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
