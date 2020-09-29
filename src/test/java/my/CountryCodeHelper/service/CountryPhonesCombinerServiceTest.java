package my.CountryCodeHelper.service;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.CountryRepoProxy;
import my.CountryCodeHelper.service.download.CountriesDownloadService;
import my.CountryCodeHelper.service.download.PhoneCodesDownloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class CountryPhonesCombinerServiceTest {
    @Mock
    CountryRepoProxy countryRepo;

    @Mock
    CountriesDownloadService countriesDownloadService;

    @Mock
    PhoneCodesDownloadService phoneCodesDownloadService;

    @InjectMocks
    CountryPhonesCombinerService combinerService;

    Set<Country> countries;

    @BeforeEach
    void setUp() {
        countries = createCountries();
        setMockOutput();
    }

    void setMockOutput() throws DownloadingException {
        doNothing().when(countriesDownloadService).execute();
        doNothing().when(phoneCodesDownloadService).execute();
        when(countryRepo.findByCountryNameContainingIgnoreCase(anyString())).thenReturn(countries);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getCombinedCountryAndPhone() {
        List<Map<String, String>> country2phonesList = combinerService.getCombinedCountryAndPhone("BR");
        assertNotEquals(0, country2phonesList.size());
        country2phonesList.forEach(e -> {
            assertEquals(3, e.size());
        });
        System.out.println(country2phonesList);
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
}