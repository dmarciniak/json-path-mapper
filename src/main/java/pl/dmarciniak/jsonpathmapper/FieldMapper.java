package pl.dmarciniak.jsonpathmapper;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.dmarciniak.jsonpathmapper.exception.JsonFieldCastException;
import pl.dmarciniak.jsonpathmapper.exception.JsonFieldNotFoundException;
import pl.dmarciniak.jsonpathmapper.exception.JsonFieldValidatorException;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class for defining mapping rule for single field
 * @param <T> type of target class
 * @param <S> type of json field
 * @param <U> type of target class field
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldMapper<T, S, U> {

    private final String jsonPath;
    private final BiFunction<T, U, T> func;
    private final Predicate<S> validator;
    private final Function<S, U> mapper;

    /**
     * Json path for mapping field
     * @param jsonPath json path, see: https://bit.ly/2JVzFRJ
     * @param originValueType json field class
     * @param <W> type of target class
     * @param <V> type of json field
     * @return json mapper builder
     */
    public static <W, V> FieldMapper<W, V, V> fromPath(String jsonPath, Class<V> originValueType) {
        return fromPath(jsonPath);
    }

    /**
     * Json path for mapping field
     * @param jsonPath json path, see: https://bit.ly/2JVzFRJ
     * @param <W> type of target class
     * @param <V> type of json field
     * @return json mapper builder
     */
    public static <W, V> FieldMapper<W, V, V> fromPath(String jsonPath) {
        return new FieldMapper<>(jsonPath, (obj, val) -> obj, val -> true, val -> val);
    }

    /**
     * Sets field value in target class.
     * @param func BiFunction that take and return instance of target class.
     * @param <W> type of target class
     * @param <V> type of value to map
     * @return json mapper builder
     */
    public <W, V> FieldMapper<W, S, V> toChainField(BiFunction<W, V, W> func) {
        return new FieldMapper<>(this.jsonPath, func, this.validator, val -> (V) val);
    }

    /**
     * Sets field value in target class
     * @param consumer BiConsumer that take instance of target class and return nothing
     * @param <W> type of target class
     * @param <V> type of value to map
     * @return json mapper builder
     */
    public <W, V> FieldMapper<W, S, V> toGetterField(BiConsumer<W, V> consumer) {
        return new FieldMapper<>(this.jsonPath, (targetObj, val) -> {consumer.accept(targetObj, val); return targetObj;}, this.validator, val -> (V) val);
    }

    /**
     * Sets field value in target class
     * @param fieldName Name of private field in target class
     * @param targetType type of value to map
     * @param <W> type of target class
     * @param <V> type of value to map
     * @return json mapper builder
     */
    public <W, V> FieldMapper<W, S, V> toPrivateField(String fieldName, Class<W> targetType) {
        return toPrivateField(fieldName);
    }

    /**
     * Sets field value in target class
     * @param fieldName Name of private field in target class
     * @param <W> type of target class
     * @param <V> type of value to map
     * @return json mapper builder
     */
    public <W, V> FieldMapper<W, S, V> toPrivateField(String fieldName) {
        return new FieldMapper<>(this.jsonPath, (targetObj, val) -> {
                try {
                    Field field = targetObj.getClass().getDeclaredField(fieldName);
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(targetObj, val);
                    field.setAccessible(accessible);
                    return targetObj;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new JsonPathException("Cannot set value to field: " + fieldName, e);
                }
            }, this.validator, val -> (V) val);
    }

    /**
     * Validator for json field value
     * @param validator taking json field value and return true or false
     * @return json mapper builder
     */
    public FieldMapper<T, S, U> withValidator(Predicate<S> validator) {
        return new FieldMapper<>(this.jsonPath, this.func, validator, this.mapper);
    }

    /**
     * Mapper for map json field value to target field
     * @param mapper taking json field value and return target field
     * @return json mapper builder
     */
    public FieldMapper<T, S, U> withMapper(Function<S, U> mapper) {
        return new FieldMapper<>(this.jsonPath, this.func, this.validator, mapper);
    }

    T run(DocumentContext json, T targetObj) {
        S rawValue = readField(json);
        validateField(rawValue);
        return mapField(targetObj, rawValue);
    }

    private S readField(DocumentContext json) {
        try {
            return json.read(jsonPath);
        } catch (PathNotFoundException e) {
            throw new JsonFieldNotFoundException("Wrong field paht: " + jsonPath, e);
        }
    }

    private void validateField(S rawValue) {
        if(!validator.test(rawValue)) {
            throw new JsonFieldValidatorException("Validator exception for path: " + jsonPath);
        }
    }

    private T mapField(T targetObj, S rawValue) {
        try {
            U mappedValue = mapper.apply(rawValue);
            return func.apply(targetObj, mappedValue);
        } catch (ClassCastException e) {
            throw new JsonFieldCastException("Wrong type of json field", e);
        }
    }
}