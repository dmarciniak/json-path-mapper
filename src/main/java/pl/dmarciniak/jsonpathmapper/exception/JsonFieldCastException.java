package pl.dmarciniak.jsonpathmapper.exception;

/**
 * Wrong json field type
 */
public class JsonFieldCastException extends JsonPathMapperException {
    public JsonFieldCastException(String message, Throwable cause) {
        super(message, cause);
    }
}
