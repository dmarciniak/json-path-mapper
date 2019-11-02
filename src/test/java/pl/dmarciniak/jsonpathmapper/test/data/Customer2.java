package pl.dmarciniak.jsonpathmapper.test.data;

import lombok.Value;
import lombok.With;

import java.time.LocalDate;

@Value
@With
public class Customer2 {

    public static Customer2 empty() {
        return new Customer2(null, null, 0, null);
    }

    private String name;
    private String surname;
    private int age;
    private LocalDate created;
}
