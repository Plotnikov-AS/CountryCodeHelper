package my.CountryCodeHelper.controller;

import my.CountryCodeHelper.service.CountryPhonesCombinerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class CodeControllerTest {

    AutoCloseable closeable;

    @Mock
    CountryPhonesCombinerService combinerService;

    @InjectMocks
    CodeController controller;

    private List<Map<String, String>> country2phones;

    @BeforeEach
    void setUp() {
        country2phones = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("countryCode", "some countryCode");
        map.put("countryName", "some countryName");
        map.put("phoneCode", "123");
        country2phones.add(map);
        setMockOutput();
    }

    void setMockOutput() {
        when(combinerService.getCombinedCountryAndPhone(anyString())).thenReturn(country2phones);
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        closeable.close();
    }

    @Test
    void showPhoneCodesForCountries() {
        ResponseEntity<Object> response = controller.showPhoneCodesForCountries("asd");
        assertEquals(200, response.getStatusCode().value());
        List<HashMap<String, String>> body = (List<HashMap<String, String>>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals(3, body.get(0).size());
        assertEquals("some countryCode", body.get(0).get("countryCode"));
        assertEquals("some countryName", body.get(0).get("countryName"));
        assertEquals("123", body.get(0).get("phoneCode"));
    }

    @Test
    void handleAllExceptions() {
        ResponseEntity<Exception> response = controller.handleAllExceptions(new RuntimeException());
        assertEquals(500, response.getStatusCode().value());
    }
}