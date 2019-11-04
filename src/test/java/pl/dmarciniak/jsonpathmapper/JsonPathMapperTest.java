package pl.dmarciniak.jsonpathmapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.dmarciniak.jsonpathmapper.exception.JsonFieldCastException;
import pl.dmarciniak.jsonpathmapper.exception.JsonFieldValidatorException;
import pl.dmarciniak.jsonpathmapper.exception.JsonParseException;
import pl.dmarciniak.jsonpathmapper.exception.JsonFieldNotFoundException;
import pl.dmarciniak.jsonpathmapper.test.data.Customer;
import pl.dmarciniak.jsonpathmapper.test.data.Customer2;
import pl.dmarciniak.jsonpathmapper.test.data.Customer3;
import pl.dmarciniak.jsonpathmapper.test.data.Customer4;
import pl.dmarciniak.jsonpathmapper.test.helper.ResourceLoader;

import java.time.LocalDate;

public class JsonPathMapperTest {
    private final static String JSON = ResourceLoader.load("json/customer.json");

    private final static String CUSTOMER_NAME_PATH = "$.customer.name";
    private final static String CUSTOMER_SURNAME_PATH = "$.customer.surname";
    private final static String CUSTOMER_AGE_PATH = "$.customer.age";
    private final static String CUSTOMER_CREATED_PATH = "$.customer.accountCreated";

    private final static String EXPECTED_NAME = "Jan";
    private final static String EXPECTED_SURNAME = "Kowalski";
    private final static int EXPECTED_AGE = 18;
    private final static LocalDate EXPECTED_CREATED_DATE = LocalDate.of(1988, 11, 20);

    @Test
    void customerTest() {
        Customer expected = new Customer();
        expected.setName(EXPECTED_NAME);
        expected.setSurname(EXPECTED_SURNAME);
        expected.setAge(EXPECTED_AGE);
        expected.setCreated(EXPECTED_CREATED_DATE);

        JsonPathMapper<Customer> mapper = JsonPathMapper.forClass(Customer.class)
                .initialize(Customer::new)
                .mapField(CUSTOMER_NAME_PATH, Customer::setName)
                .mapField(CUSTOMER_SURNAME_PATH, Customer::setSurname)
                .mapField(FieldMapper.fromPath(CUSTOMER_AGE_PATH, Integer.class).toGetterField(Customer::setAge).withValidator(val -> val > 0))
                .mapField(FieldMapper.fromPath(CUSTOMER_CREATED_PATH, String.class).toGetterField(Customer::setCreated).withMapper(LocalDate::parse))
                .build();

        Assertions.assertEquals(expected, mapper.map(JSON));
    }

    @Test
    void customer2Test() {
        Customer2 expected = Customer2.empty()
                .withName(EXPECTED_NAME)
                .withSurname(EXPECTED_SURNAME)
                .withAge(EXPECTED_AGE)
                .withCreated(EXPECTED_CREATED_DATE);

        JsonPathMapper<Customer2> mapper = JsonPathMapper.forClass(Customer2.class)
                .initialize(Customer2::empty)
                .mapField(CUSTOMER_NAME_PATH, Customer2::withName)
                .mapField(CUSTOMER_SURNAME_PATH, Customer2::withSurname)
                .mapField(FieldMapper.fromPath(CUSTOMER_AGE_PATH, Integer.class).toChainField(Customer2::withAge).withValidator(val -> val > 0))
                .mapField(FieldMapper.fromPath(CUSTOMER_CREATED_PATH, String.class).toChainField(Customer2::withCreated).withMapper(LocalDate::parse))
                .build();

        Assertions.assertEquals(expected, mapper.map(JSON));
    }

    @Test
    void customer3Test() {
        Customer3 expected = Customer3.builder()
                .name(EXPECTED_NAME)
                .surname(EXPECTED_SURNAME)
                .age(EXPECTED_AGE)
                .created(EXPECTED_CREATED_DATE)
                .build();

        JsonPathMapper<Customer3> mapper = JsonPathMapper.forClass(Customer3.Customer3Builder.class)
                .initialize(Customer3::builder)
                .mapField(CUSTOMER_NAME_PATH, Customer3.Customer3Builder::name)
                .mapField(CUSTOMER_SURNAME_PATH, Customer3.Customer3Builder::surname)
                .mapField(FieldMapper.fromPath(CUSTOMER_AGE_PATH, Integer.class).toChainField(Customer3.Customer3Builder::age).withValidator(val -> val > 0))
                .mapField(FieldMapper.fromPath(CUSTOMER_CREATED_PATH, String.class).toChainField(Customer3.Customer3Builder::created).withMapper(LocalDate::parse))
                .buildWithResultMapper(Customer3.Customer3Builder::build);

        Assertions.assertEquals(expected, mapper.map(JSON));
    }

    @Test
    void customer3ParallelTest() {
        Customer3 expected = Customer3.builder()
                .name(EXPECTED_NAME)
                .surname(EXPECTED_SURNAME)
                .age(EXPECTED_AGE)
                .created(EXPECTED_CREATED_DATE)
                .build();

        JsonPathMapper<Customer3> mapper = JsonPathMapper.forClass(Customer3.Customer3Builder.class)
                .initialize(Customer3::builder)
                .mapField(CUSTOMER_NAME_PATH, Customer3.Customer3Builder::name)
                .mapField(CUSTOMER_SURNAME_PATH, Customer3.Customer3Builder::surname)
                .mapField(FieldMapper.fromPath(CUSTOMER_AGE_PATH, Integer.class).toChainField(Customer3.Customer3Builder::age).withValidator(val -> val > 0))
                .mapField(FieldMapper.fromPath(CUSTOMER_CREATED_PATH, String.class).toChainField(Customer3.Customer3Builder::created).withMapper(LocalDate::parse))
                .buildWithResultMapper(Customer3.Customer3Builder::build);

        Assertions.assertEquals(expected, mapper.parallelMap(JSON));
    }

    @Test
    void customer4Test() {
        Customer4 expected = new Customer4(EXPECTED_NAME, EXPECTED_SURNAME, EXPECTED_AGE, EXPECTED_CREATED_DATE);

        JsonPathMapper<Customer4> mapper = JsonPathMapper.forClass(Customer4.class)
                .mapField(CUSTOMER_NAME_PATH, "name")
                .mapField(CUSTOMER_SURNAME_PATH, "surname")
                .mapField(FieldMapper.fromPath(CUSTOMER_AGE_PATH, Integer.class).toPrivateField("age", Customer4.class).withValidator(val -> val > 0))
                .mapField(FieldMapper.fromPath(CUSTOMER_CREATED_PATH, String.class).toPrivateField("created", Customer4.class).withMapper(LocalDate::parse))
                .build();

        Assertions.assertEquals(expected, mapper.map(JSON));
    }

    @Test
    void customerTestWhenValidationFail() {
        JsonPathMapper<Customer> mapper = JsonPathMapper.forClass(Customer.class)
                .initialize(Customer::new)
                .mapField(FieldMapper.fromPath(CUSTOMER_AGE_PATH, Integer.class).toGetterField(Customer::setAge).withValidator(val -> val < 0))
                .build();

        Assertions.assertThrows(JsonFieldValidatorException.class, () -> mapper.map(JSON));
    }

    @Test
    void customerTestWhenJsonParsingFail() {
        JsonPathMapper<Customer> mapper = JsonPathMapper.forClass(Customer.class)
                .initialize(Customer::new)
                .mapField(CUSTOMER_NAME_PATH, Customer::setName)
                .build();

        Assertions.assertThrows(JsonParseException.class, () -> mapper.map("{NO_JSON"));
    }

    @Test
    void customerTestWhenPathNotFound() {
        JsonPathMapper<Customer> mapper = JsonPathMapper.forClass(Customer.class)
                .initialize(Customer::new)
                .mapField("$.wrong.path", Customer::setName)
                .build();

        Assertions.assertThrows(JsonFieldNotFoundException.class, () -> mapper.map(JSON));
    }

    @Test
    void customerTestWhenWrongFieldType() {
        JsonPathMapper<Customer> mapper = JsonPathMapper.forClass(Customer.class)
                .initialize(Customer::new)
                .mapField(CUSTOMER_NAME_PATH, Customer::setAge)
                .build();

        Assertions.assertThrows(JsonFieldCastException.class, () -> mapper.map(JSON));
    }

    @Test
    void customerTestWithOptionalFields() {
        JsonPathMapper<Customer> mapper = JsonPathMapper.forClass(Customer.class)
                .initialize(Customer::new)
                .mapOptionalField(CUSTOMER_NAME_PATH, Customer::setName)
                .mapOptionalField("$.customer.noexistfield", Customer::setSurname)
                .build();

        Customer cust = mapper.map(JSON);
        Assertions.assertEquals(EXPECTED_NAME, cust.getName());
        Assertions.assertNull(cust.getSurname());
    }
}
