package my.CountryCodeHelper.service.download;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ErrorCode;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.CountryRepoProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@SpringBootTest
class CountriesDownloadServiceTest {
    @Mock
    CountryRepoProxy countryRepo;

    @InjectMocks
    CountriesDownloadService countriesDownloadService;


    String correctCountryCode;
    Country correctCountry;
    PhoneCode correctPhoneCode;
    ExtResponse failureResponse;
    ExtResponse badResponse;
    ExtResponse goodResponse;

    @BeforeEach
    void setUp() {
        correctCountryCode = "BE";
        correctPhoneCode = new PhoneCode();
        correctPhoneCode.setId(1L);
        correctPhoneCode.setCountryCode(correctCountryCode);
        correctPhoneCode.setPhoneCode("32");
        correctPhoneCode.setCountry(correctCountry);

        correctCountry = new Country();
        correctCountry.setId(1L);
        correctCountry.setCountryName("Belgium");
        correctCountry.setCountryCode(correctCountryCode);
        correctCountry.setPhoneCode(correctPhoneCode);
        correctCountry.setUpdTime(new Date(System.currentTimeMillis()));

        failureResponse = new ExtResponse();
        badResponse = new ExtResponse();
        goodResponse = new ExtResponse();

        failureResponse.setErrorCode(ErrorCode.ERROR_CODE_FAILURE);

        goodResponse.setErrorCode(ErrorCode.ERROR_CODE_OK);
        goodResponse.setReceivedData(new ByteArrayInputStream("{\"BD\": \"Bangladesh\", \"BE\": \"Belgium\", \"BF\": \"Burkina Faso\"}".getBytes()));

        badResponse.setErrorCode(ErrorCode.ERROR_CODE_OK);
        badResponse.setReceivedData(new ByteArrayInputStream("some incorrect string".getBytes()));

        setMockOutput();
    }

    void setMockOutput() {
        when(countryRepo.getByCountryCode(anyString())).thenReturn(correctCountry);
        doNothing().when(countryRepo).save(any());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void updateDataWithFailureResponse() {
        assertThrows(DownloadingException.class, () -> countriesDownloadService.updateData(failureResponse));
    }

    @Test
    void updateDataWithIncorrectStringResponse() {
        assertThrows(DownloadingException.class, () -> countriesDownloadService.updateData(badResponse));
    }

    @Test
    void updateDataWithNullReceivedData() {
        badResponse.setReceivedData(null);
        assertThrows(DownloadingException.class, () -> countriesDownloadService.updateData(badResponse));
    }

    @Test
    void updateDataWithEmptyStringResponse() {
        badResponse.setReceivedData(new ByteArrayInputStream("".getBytes()));
        assertThrows(DownloadingException.class, () -> countriesDownloadService.updateData(badResponse));
    }

    @Test
    void updateDataWithCorrectResponse() throws DownloadingException {
        countriesDownloadService.updateData(goodResponse);
    }
}