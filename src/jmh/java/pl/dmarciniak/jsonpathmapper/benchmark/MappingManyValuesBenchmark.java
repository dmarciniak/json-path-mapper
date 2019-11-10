package pl.dmarciniak.jsonpathmapper.benchmark;

import org.openjdk.jmh.annotations.*;
import pl.dmarciniak.jsonpathmapper.FieldMapper;
import pl.dmarciniak.jsonpathmapper.JsonPathMapper;
import pl.dmarciniak.jsonpathmapper.JsonPathMapperBuilder;
import pl.dmarciniak.jsonpathmapper.benchmark.data.helper.JmhResourceLoader;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(1)
public class MappingManyValuesBenchmark {

    @Param({"5", "10", "50", "100", "500", "1000"})
    private int valuesAmount;

    @Param({"0", "1"})
    private int fieldMappingTimeMs;

    private String json;

    private JsonPathMapper<Integer> mapper;

    private Function<Integer, Integer> fieldMapperEmulator;

    @Setup
    public void before() {
        json = JmhResourceLoader.load("json/big.json");

        fieldMapperEmulator = (i) -> {
            try {
                Thread.sleep(fieldMappingTimeMs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return i;
        };

        JsonPathMapperBuilder<AtomicInteger> builder = JsonPathMapper.forClass(AtomicInteger.class).initialize(AtomicInteger::new);
        for (int i = 1; i <= valuesAmount; ++i) {
            builder.mapField(FieldMapper.fromPath("$.test.i" + i, Integer.class).toGetterField(AtomicInteger::addAndGet).withMapper(fieldMapperEmulator));
        }
        mapper = builder.buildWithResultMapper(AtomicInteger::get);

        assertThat(mapper.map(json)).isEqualTo(((1 + valuesAmount) * valuesAmount) / 2);
        assertThat(mapper.parallelMap(json)).isEqualTo(((1 + valuesAmount) * valuesAmount) / 2);

    }

    @Benchmark
    public Integer sequentialMap() {
        return mapper.map(json);
    }

    @Benchmark
    public Integer parallelMap() {
        return mapper.parallelMap(json);
    }
}
