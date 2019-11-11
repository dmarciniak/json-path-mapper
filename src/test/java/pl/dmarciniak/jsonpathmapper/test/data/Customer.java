package pl.dmarciniak.jsonpathmapper.test.data;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Customer {
    private String name;
    private String surname;
    private int age;
    private LocalDate created;
    private List<String> phones;
}
