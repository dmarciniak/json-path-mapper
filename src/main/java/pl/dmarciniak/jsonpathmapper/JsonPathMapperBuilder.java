package pl.dmarciniak.jsonpathmapper;

import pl.dmarciniak.jsonpathmapper.exception.JsonPathMapperException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builder for json mapper
 * @param <T>
 */
public final class JsonPathMapperBuilder<T> {

    private final Class<T> targetType;
    private final List<FieldMapper<T, ?, ?>> fieldMappers = new ArrayList<>();
    private Supplier<T> initializer;

    JsonPathMapperBuilder(Class<T> targetType) {
        this.targetType = targetType;
        this.initializer = () -> {
            try {
                return targetType.newInstance();
            } catch (Exception e) {
                throw new JsonPathMapperException("Cannot create new instance of " + targetType.getSimpleName(), e);
            }
        };
    }

    /**
     * Function take supplier with return instance of target class
     * @param initializer supplier with initialize instance of target class
     * @return json mapper builder
     */
    public JsonPathMapperBuilder<T> initialize(Supplier<T> initializer) {
        this.initializer = initializer;
        return this;
    }

    /**
     * Function to map single field
     * @param fieldMapper instance
     * @return json mapper builder
     */
    public JsonPathMapperBuilder<T> mapField(FieldMapper<T, ?, ?> fieldMapper) {
        fieldMappers.add(fieldMapper);
        return this;
    }

    /**
     * Function to map single field
     * Equivalent of: FieldMapper.fromPath(...).toChainField(...)
     * @return json mapper builder
     */
    public <S> JsonPathMapperBuilder<T> mapField(String jsonPath, BiFunction<T, S, T> func) {
        fieldMappers.add(FieldMapper.fromPath(jsonPath).toChainField(func));
        return this;
    }

    /**
     * Function to optionally map single field
     * Equivalent of: FieldMapper.fromPath(...).optional().toChainField(...)
     * @return json mapper builder
     */
    public <S> JsonPathMapperBuilder<T> mapOptionalField(String jsonPath, BiFunction<T, S, T> func) {
        fieldMappers.add(FieldMapper.fromPath(jsonPath).optional().toChainField(func));
        return this;
    }

    /**
     * Function to map single field
     * Equivalent of: FieldMapper.fromPath(...).toGetterField(...)
     * @return json mapper builder
     */
    public <S> JsonPathMapperBuilder<T> mapField(String jsonPath, BiConsumer<T, S> consumer) {
        fieldMappers.add(FieldMapper.fromPath(jsonPath).toGetterField(consumer));
        return this;
    }

    /**
     * Function to optionally map single field
     * Equivalent of: FieldMapper.fromPath(...).optional().toGetterField(...)
     * @return json mapper builder
     */
    public <S> JsonPathMapperBuilder<T> mapOptionalField(String jsonPath, BiConsumer<T, S> consumer) {
        fieldMappers.add(FieldMapper.fromPath(jsonPath).optional().toGetterField(consumer));
        return this;
    }

    /**
     * Function to map single field
     * Equivalent of: FieldMapper.fromPath(...).toPrivateField(...)
     * @return json mapper builder
     */
    public <S> JsonPathMapperBuilder<T> mapField(String jsonPath, String targetFieldName) {
        fieldMappers.add(FieldMapper.fromPath(jsonPath).toPrivateField(targetFieldName));
        return this;
    }

    /**
     * Function to optionally map single field
     * Equivalent of: FieldMapper.fromPath(...).optional().toPrivateField(...)
     * @return json mapper builder
     */
    public <S> JsonPathMapperBuilder<T> mapOptionalField(String jsonPath, String targetFieldName) {
        fieldMappers.add(FieldMapper.fromPath(jsonPath).optional().toPrivateField(targetFieldName));
        return this;
    }

    /**
     * Build json mapper
     * @return
     */
    public JsonPathMapper<T> build() {
        return new JsonPathMapper<T>(initializer, fieldMappers);
    }

    /**
     * Build json mapper with additional map of result
     * @param resultMapper
     * @param <S> type of new target class
     * @return json mapper with new target class
     */
    public <S> JsonPathMapper<S> buildWithResultMapper(Function<T, S> resultMapper) {
        return new JsonPathMapper<S>(null, null) {
            private JsonPathMapper<T> mapper = build();

            @Override
            public S map(String jsonStr) {
                return resultMapper.apply(mapper.map(jsonStr));
            }
        };
    }
}
