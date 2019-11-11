package pl.dmarciniak.jsonpathmapper.test.data;

import lombok.Value;
import lombok.With;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Value
@With
public class Customer2 {

    public static Customer2 empty() {
        return new Customer2(null, null, 0, null, Collections.emptyList());
    }

    private String name;
    private String surname;
    private int age;
    private LocalDate created;
    private List<String> phones;
}
