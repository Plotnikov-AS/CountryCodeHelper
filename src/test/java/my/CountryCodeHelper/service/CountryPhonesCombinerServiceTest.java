package my.CountryCodeHelper.service;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.CountryRepoProxy;
import my.CountryCodeHelper.repo.proxy.PhoneCodeRepoProxy;
import my.CountryCodeHelper.service.download.CountriesDownloadService;
import my.CountryCodeHelper.service.download.PhoneCodesDownloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class CountryPhonesCombinerServiceTest {
    @Mock
    CountryRepoProxy countryRepo;

    @Mock
    PhoneCodeRepoProxy phoneCodeRepo;

    @Mock
    CountriesDownloadService countriesDownloadService;

    @Mock
    PhoneCodesDownloadService phoneCodesDownloadService;

    @InjectMocks
    CountryPhonesCombinerService combinerService;

    Set<Country> countries;
    Map<String, String> codes2countries;
    Map<String, String> phones2codes;
    PhoneCode phoneCode;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        countries = createCountries();
        phoneCode = new PhoneCode();
        phoneCode.setId(1L);
        phoneCode.setCountryCode("CD");
        phoneCode.setPhoneCode("123");

        if (testInfo.getTestMethod().get().getName().equalsIgnoreCase("getCombinedCountryAndPhone")) {
            doNothing().when(countriesDownloadService).execute();
            doNothing().when(phoneCodesDownloadService).execute();
            when(phoneCodeRepo.getByCountryCode(anyString())).thenReturn(phoneCode);
            when(countryRepo.findByCountryNameContainingIgnoreCase(anyString())).thenReturn(countries);

        } else if (testInfo.getTestMethod().get().getName().equalsIgnoreCase("getCombinedWhenDatabaseUnavailable")) {
            when(phoneCodeRepo.getByCountryCode(anyString())).thenReturn(phoneCode);
            doThrow(new DataAccessResourceFailureException("")).when(countriesDownloadService).execute();
            doThrow(new DataAccessResourceFailureException("")).when(phoneCodesDownloadService).execute();
            when(countryRepo.findByCountryNameContainingIgnoreCase(anyString())).thenThrow(DataAccessResourceFailureException.class);

        } else if (testInfo.getTestMethod().get().getName().equalsIgnoreCase("getCombinedWhenExtSystemUnavailable")) {
            when(phoneCodeRepo.getByCountryCode(anyString())).thenReturn(phoneCode);
            doThrow(DownloadingException.class).when(countriesDownloadService).execute();
            doThrow(DownloadingException.class).when(phoneCodesDownloadService).execute();
            when(countryRepo.findByCountryNameContainingIgnoreCase(anyString())).thenReturn(countries);

        }

        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getCombinedCountryAndPhone() {
        List<Map<String, String>> country2phonesList = combinerService.getCombinedCountryAndPhone("BR");
        assertNotEquals(0, country2phonesList.size());
        country2phonesList.forEach(e -> {
            assertEquals(3, e.size());
        });
    }

    @Test
    void getCombinedWhenDatabaseUnavailable() {
        assertEquals(ArrayList.class, combinerService.getCombinedCountryAndPhone("BR").getClass());
    }

    @Test
    void getCombinedWhenExtSystemUnavailable() {
        List<Map<String, String>> country2phonesList = combinerService.getCombinedCountryAndPhone("BR");
        assertNotEquals(0, country2phonesList.size());
        country2phonesList.forEach(e -> {
            assertEquals(3, e.size());
        });
    }

    private Set<Country> createCountries() {
        Set<Country> countries = new HashSet<>();
        for (long i = 0; i < 5; i++) {
            Country country = new Country();
            country.setId(i);
            country.setCountryCode("CD" + i);
            country.setCountryName("cntrNm");
            PhoneCode phoneCode = new PhoneCode();
            phoneCode.setId(i);
            phoneCode.setCountryCode("CD" + i);
            phoneCode.setPhoneCode("123");
            phoneCode.setCountry(country);
            country.setPhoneCode(phoneCode);
            country.setUpdTime(new Date(System.currentTimeMillis()));
            countries.add(country);
        }
        return countries;
    }

    private Map<String, String> createCodes2countries() {
        Map<String, String> codes2countries = new HashMap<>();
        codes2countries.put("CD", "someName");
        codes2countries.put("FD", "moreName");
        return codes2countries;
    }

    private Map<String, String> createPhones2codes() {
        Map<String, String> codes2countries = new HashMap<>();
        codes2countries.put("CD", "123");
        codes2countries.put("FD", "435");
        return codes2countries;
    }
}