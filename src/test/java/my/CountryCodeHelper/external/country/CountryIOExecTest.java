package my.CountryCodeHelper.external.country;

import my.CountryCodeHelper.external.ExtRequest;
import my.CountryCodeHelper.external.ExtResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;

import static my.CountryCodeHelper.external.ErrorCode.ERROR_CODE_FAILURE;
import static my.CountryCodeHelper.external.ErrorCode.ERROR_CODE_OK;
import static my.CountryCodeHelper.external.ExtMethods.GET_COUNTRY_NAMES;
import static my.CountryCodeHelper.external.ExtMethods.GET_PHONE_CODES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class CountryIOExecTest {
    @Mock
    ExtRequest request;

    CountryIO countryIO = mock(CountryIO.class);

    @InjectMocks
    CountryIOExec exec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void executeWithSystemUnavailable() {
        when(request.getMethod()).thenReturn(GET_COUNTRY_NAMES);
        when(countryIO.isSystemAvailable()).thenReturn(false);
        ExtResponse response = exec.execute();
        assertEquals(ERROR_CODE_FAILURE, response.getErrorCode());
        assertNull(response.getReceivedData());
    }

    @Test
    void executeWithSystemAvailableAndEmptyData() {
        when(request.getMethod()).thenReturn(GET_COUNTRY_NAMES);
        when(countryIO.isSystemAvailable()).thenReturn(true);
        when(countryIO.getData()).thenReturn(null);
        ExtResponse response = exec.execute();
        assertEquals(ERROR_CODE_FAILURE, response.getErrorCode());
        assertNull(response.getReceivedData());
    }

    @Test
    void executeWithSystemAvailableForPhonesData() {
        when(request.getMethod()).thenReturn(GET_PHONE_CODES);
        when(countryIO.isSystemAvailable()).thenReturn(true);
        when(countryIO.getData()).thenReturn(new ByteArrayInputStream("{\"BD\": \"880\", \"BE\": \"32\"}".getBytes()));
        ExtResponse response = exec.execute();
        assertEquals(ERROR_CODE_OK, response.getErrorCode());
        assertNotNull(response.getReceivedData());
    }

    @Test
    void executeWithSystemAvailableForCountryNamesData() {
        when(request.getMethod()).thenReturn(GET_COUNTRY_NAMES);
        when(countryIO.isSystemAvailable()).thenReturn(true);
        when(countryIO.getData()).thenReturn(new ByteArrayInputStream("{\"BD\": \"Bangladesh\", \"BE\": \"Belgium\"}".getBytes()));
        ExtResponse response = exec.execute();
        assertEquals(ERROR_CODE_OK, response.getErrorCode());
        assertNotNull(response.getReceivedData());
    }
}