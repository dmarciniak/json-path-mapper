package pl.dmarciniak.jsonpathmapper.test.data;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Customer4 {
    private String name;
    private String surname;
    private int age;
    private LocalDate created;
}
