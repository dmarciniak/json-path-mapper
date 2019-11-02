package pl.dmarciniak.jsonpathmapper.exception;

/**
 * Wrong json path
 */
public class JsonFieldNotFoundException extends JsonPathMapperException {
    public JsonFieldNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
