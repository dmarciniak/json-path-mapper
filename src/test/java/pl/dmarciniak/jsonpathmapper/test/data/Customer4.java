package pl.dmarciniak.jsonpathmapper.test.data;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    private List<String> phones;
}
