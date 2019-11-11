package pl.dmarciniak.jsonpathmapper.test.data;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Customer3 {
    private String name;
    private String surname;
    private int age;
    private LocalDate created;
    private List<String> phones;
}
