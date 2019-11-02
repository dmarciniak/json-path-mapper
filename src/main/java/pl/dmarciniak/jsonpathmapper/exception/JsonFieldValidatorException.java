package pl.dmarciniak.jsonpathmapper.exception;

/**
 * Generic exception for mapper classes
 */
public class JsonFieldValidatorException extends JsonPathMapperException {
    public JsonFieldValidatorException(String message) {
        super(message);
    }
}
