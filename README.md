# json-path-mapper
Json to Java class mapper based on json path syntax.
* fast and easy to use
* support & required java 8
* support all types of data classes: dto, value object, builders, etc

For *json path* syntax examples see: https://bit.ly/2JVzFRJ

## Examples of usages:
In below examples I used lombok, but of course it isn't necessary.
For people who don't know lombok I also added example of creating instances of these data classes.

### Example 1:
Data class:
```java
@Data
public class Customer {
    private String name;
    private String surname;
    private int age;
    private LocalDate created;
}
```
Creating instance of this data class:
```java
Customer cust = new Customer();
cust.setName(EXPECTED_NAME);
cust.setSurname(EXPECTED_SURNAME);
cust.setAge(EXPECTED_AGE);
cust.setCreated(EXPECTED_CREATED_DATE);
```
Usage of json-path-mapper:
```java
JsonPathMapper<Customer> mapper = JsonPathMapper.forClass(Customer.class)
   .initialize(Customer::new)
   .mapField("$.customer.name", Customer::setName)
   .mapField("$.customer.surname", Customer::setSurname)
   .mapField(FieldMapper.fromPath("$.customer.age", Integer.class).toGetterField(Customer::setAge).withValidator(val -> val > 0))
   .mapField(FieldMapper.fromPath("$.customer.accountCreated", String.class).toGetterField(Customer::setCreated).withMapper(LocalDate::parse))
   .build();

Customer cust = mapper.map(JSON);
```

### Example 2:
Data class:
```java
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
```
Creating instance of this data class:
```java
Customer2 cust = Customer2.empty()
    .withName(EXPECTED_NAME)
    .withSurname(EXPECTED_SURNAME)
    .withAge(EXPECTED_AGE)
    .withCreated(EXPECTED_CREATED_DATE);
```
Usage of json-path-mapper:
```java
JsonPathMapper<Customer2> mapper = JsonPathMapper.forClass(Customer2.class)
    .initialize(Customer2::empty)
    .mapField("$.customer.name", Customer2::withName)
    .mapField("$.customer.surname", Customer2::withSurname)
    .mapField(FieldMapper.fromPath("$.customer.age", Integer.class).toChainField(Customer2::withAge).withValidator(val -> val > 0))
    .mapField(FieldMapper.fromPath("$.customer.accountCreated", String.class).toChainField(Customer2::withCreated).withMapper(LocalDate::parse))
    .build();

Customer cust = mapper.map(JSON);
```

### Example 3:
Data class:
```java
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Customer3 {
    private String name;
    private String surname;
    private int age;
    private LocalDate created;
}
```
Creating instance of this data class:
```java
Customer3 cust = Customer3.builder()
    .name(EXPECTED_NAME)
    .surname(EXPECTED_SURNAME)
    .age(EXPECTED_AGE)
    .created(EXPECTED_CREATED_DATE)
    .build();
```
Usage of json-path-mapper:
```java
JsonPathMapper<Customer3> mapper = JsonPathMapper.forClass(Customer3.Customer3Builder.class)
    .initialize(Customer3::builder)
    .mapField("$.customer.name", Customer3.Customer3Builder::name)
    .mapField("$.customer.surname", Customer3.Customer3Builder::surname)
    .mapField(FieldMapper.fromPath("$.customer.age", Integer.class).toChainField(Customer3.Customer3Builder::age).withValidator(val -> val > 0))
    .mapField(FieldMapper.fromPath("$.customer.accountCreated", String.class).toChainField(Customer3.Customer3Builder::created).withMapper(LocalDate::parse))
    .buildWithResultMapper(Customer3.Customer3Builder::build);

Customer cust = mapper.map(JSON);
```

### Example 4:
Data class:
```java
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
```
Creating instance of this data class:
```java
Customer4 cust = new Customer4(EXPECTED_NAME, EXPECTED_SURNAME, EXPECTED_AGE, EXPECTED_CREATED_DATE);
```
Usage of json-path-mapper:
```java
JsonPathMapper<Customer4> mapper = JsonPathMapper.forClass(Customer4.class)
    .mapField("$.customer.name", "name")
    .mapField("$.customer.surname", "surname")
    .mapField(FieldMapper.fromPath("$.customer.age", Integer.class).toPrivateField("age", Customer4.class).withValidator(val -> val > 0))
    .mapField(FieldMapper.fromPath("$.customer.accountCreated", String.class).toPrivateField("created", Customer4.class).withMapper(LocalDate::parse))
    .build();

Customer cust = mapper.map(JSON);
```
