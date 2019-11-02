package pl.dmarciniak.jsonpathmapper.test.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Customer {
    private String name;
    private String surname;
    private int age;
    private LocalDate created;
}
