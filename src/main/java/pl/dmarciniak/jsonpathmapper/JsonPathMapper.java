package pl.dmarciniak.jsonpathmapper;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import pl.dmarciniak.jsonpathmapper.exception.JsonParseException;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Class for mapping (string) json to target (T) class.
 * Use "forClass" static method to initialize builder.
 * @param <T> type of target class
 */
@AllArgsConstructor
public class JsonPathMapper<T> {

    private final Supplier<T> initializer;
    private final List<FieldMapper<T, ?, ?>> fieldMappers;

    /**
     * Method return builder for creating mapper
     * @param targetType target class
     * @param <S> type of target class
     * @return builder for creating mapper
     */
    public static <S> JsonPathMapperBuilder<S> forClass(Class<S> targetType) {
        return new JsonPathMapperBuilder<S>(targetType);
    }

    /**
     * Method map json to target class
     * @param jsonStr String with source json
     * @return instance of target class with mapped data
     */
    public T map(String jsonStr) {
        return mapStream(jsonStr, fieldMappers.stream());
    }

    /**
     * Method parallel map json to target class
     * @param jsonStr String with source json
     * @return instance of target class with mapped data
     */
    public T parallelMap(String jsonStr) {
        return mapStream(jsonStr, fieldMappers.parallelStream());
    }

    private T mapStream(String jsonStr, Stream<FieldMapper<T, ?, ?>> stream) {
        DocumentContext json = parseJson(jsonStr);
        T targetObj = initializer.get();
        return stream.map(mapper -> mapper.getMapFunc(json))
                .reduce(Function.identity(), Function::andThen)
                .apply(targetObj);
    }

    private DocumentContext parseJson(String jsonStr) {
        try {
            return JsonPath.parse(jsonStr);
        } catch (InvalidJsonException e) {
            throw new JsonParseException("Wrong json format", e);
        }
    }
}
