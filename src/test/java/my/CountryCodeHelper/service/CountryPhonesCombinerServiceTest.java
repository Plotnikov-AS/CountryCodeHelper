package my.CountryCodeHelper.service;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.CountryRepoProxy;
import my.CountryCodeHelper.repo.proxy.PhoneCodeRepoProxy;
import my.CountryCodeHelper.service.data.DataService;
import my.CountryCodeHelper.service.data.download.CountriesDownloadService;
import my.CountryCodeHelper.service.data.download.DataDownloadService;
import my.CountryCodeHelper.service.data.download.PhoneCodesDownloadService;
import my.CountryCodeHelper.service.data.refresh.Refresher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class CountryPhonesCombinerServiceTest {
    AutoCloseable closeable;

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
    PhoneCode phoneCode;

    @BeforeEach
    void setUp() {
        countries = createCountries();
        phoneCode = new PhoneCode();
        phoneCode.setId(1L);
        phoneCode.setCountryCode("CD");
        phoneCode.setPhoneCode("123");
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        closeable.close();
    }

    @Test
    void getCombinedCountryAndPhone() {
        try (MockedStatic<Refresher> mockedRefresher = mockStatic(Refresher.class)) {
            combinerService = new CountryPhonesCombinerService(countryRepo, phoneCodeRepo, countriesDownloadService, phoneCodesDownloadService);
            mockedRefresher.when(() -> Refresher.refresh(any(DataDownloadService.class))).thenAnswer(invocationOnMock -> null);
            mockedRefresher.when(Refresher::isCountriesRefreshRunning).thenReturn(false);
            assertFalse(Refresher.isCountriesRefreshRunning());
            mockedRefresher.when(Refresher::isPhonesRefreshRunning).thenReturn(false);
            assertFalse(Refresher.isCountriesRefreshRunning());
            when(countryRepo.findByCountryNameContainingIgnoreCase(anyString())).thenReturn(countries);
            when(phoneCodeRepo.getByCountryCode(anyString())).thenReturn(phoneCode);
            List<Map<String, String>> country2phonesList = combinerService.getCombinedCountryAndPhone("BR");
            assertNotEquals(0, country2phonesList.size());
            country2phonesList.forEach(e -> assertEquals(3, e.size()));
        }
    }

    @Test
    void getCombinedWhenDatabaseUnavailable() {
        combinerService = new CountryPhonesCombinerService(countryRepo, phoneCodeRepo, countriesDownloadService, phoneCodesDownloadService);
        ExtResponse countriesResponse = new ExtResponse();
        countriesResponse.setReceivedData(new ByteArrayInputStream("{\"BD\": \"Bangladesh\"}".getBytes()));
        when(countriesDownloadService.downloadData()).thenReturn(countriesResponse);
        ExtResponse phonesResponse = new ExtResponse();
        phonesResponse.setReceivedData(new ByteArrayInputStream("{\"BD\": \"880\"}".getBytes()));
        when(phoneCodesDownloadService.downloadData()).thenReturn(phonesResponse);
        when(countryRepo.findByCountryNameContainingIgnoreCase(anyString())).thenThrow(new DataAccessResourceFailureException("err"));
        List<Map<String, String>> result = combinerService.getCombinedCountryAndPhone("ba");
        assertEquals(ArrayList.class, result.getClass());
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).size());
        assertEquals("BD", result.get(0).get("countryCode"));
        assertEquals("Bangladesh", result.get(0).get("countryName"));
        assertEquals("880", result.get(0).get("phoneCode"));
    }

    @Test
    void getCombinedWhenExtSystemUnavailable() {
        try (MockedStatic<Refresher> mockedRefresher = mockStatic(Refresher.class)) {
            mockedRefresher.when(Refresher::isCountriesRefreshRunning).thenReturn(false);
            assertFalse(Refresher.isCountriesRefreshRunning());
            mockedRefresher.when(() -> Refresher.refresh(any(DataDownloadService.class))).thenAnswer(invocationOnMock -> null);
            mockedRefresher.when(() -> Refresher.isRefreshRunning(DataService.class)).thenReturn(false);
            assertFalse(Refresher.isRefreshRunning(DataService.class));
            when(countryRepo.findByCountryNameContainingIgnoreCase(anyString())).thenReturn(countries);
            combinerService = new CountryPhonesCombinerService(countryRepo, phoneCodeRepo, countriesDownloadService, phoneCodesDownloadService);
            List<Map<String, String>> result = combinerService.getCombinedCountryAndPhone("ba");
            assertEquals(ArrayList.class, result.getClass());
            assertEquals(1, result.size());
            assertEquals(3, result.get(0).size());
            assertEquals("BD", result.get(0).get("countryCode"));
            assertEquals("Bangladesh", result.get(0).get("countryName"));
            assertEquals("880", result.get(0).get("phoneCode"));
        }
    }

    @Test
    void getCombinedWhenExtSystemAndDatabaseUnavailable() {
        combinerService = new CountryPhonesCombinerService(countryRepo, phoneCodeRepo, countriesDownloadService, phoneCodesDownloadService);
        when(countryRepo.findByCountryNameContainingIgnoreCase(anyString())).thenThrow(new DataAccessResourceFailureException("err"));
        when(countriesDownloadService.downloadData()).thenThrow(new DownloadingException("err"));
        assertThrows(DownloadingException.class, () -> combinerService.getCombinedCountryAndPhone("be"));
    }

    private Set<Country> createCountries() {
        Set<Country> countries = new HashSet<>();
        Country country = new Country();
        country.setId(1L);
        country.setCountryCode("BD");
        country.setCountryName("Bangladesh");
        PhoneCode phoneCode = new PhoneCode();
        phoneCode.setId(1L);
        phoneCode.setCountryCode("BD");
        phoneCode.setPhoneCode("880");
        phoneCode.setCountry(country);
        country.setPhoneCode(phoneCode);
        countries.add(country);
        return countries;
    }
}