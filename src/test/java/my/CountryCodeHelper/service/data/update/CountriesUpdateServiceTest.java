package my.CountryCodeHelper.service.data.update;

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

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CountriesUpdateServiceTest {

    @Mock
    CountryRepoProxy countryRepo;

    @InjectMocks
    CountriesUpdateService countriesUpdateService;

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

        failureResponse = new ExtResponse();
        badResponse = new ExtResponse();
        goodResponse = new ExtResponse();

        failureResponse.setErrorCode(ErrorCode.ERROR_CODE_FAILURE);

        goodResponse.setErrorCode(ErrorCode.ERROR_CODE_OK);
        goodResponse.setReceivedData(new ByteArrayInputStream("{\"BD\": \"Bangladesh\", \"BE\": \"Belgium\", \"BF\": \"Burkina Faso\"}".getBytes()));

        badResponse.setErrorCode(ErrorCode.ERROR_CODE_OK);
        badResponse.setReceivedData(new ByteArrayInputStream("some incorrect string".getBytes()));
    }

    @Test
    void updateDataWithFailureResponse() {
        countriesUpdateService.setResponse(failureResponse);
        assertThrows(DownloadingException.class, () -> countriesUpdateService.update());
    }

    @Test
    void updateDataWithIncorrectStringResponse() {
        countriesUpdateService.setResponse(badResponse);
        assertThrows(DownloadingException.class, () -> countriesUpdateService.update());
    }

    @Test
    void updateDataWithNullReceivedData() {
        countriesUpdateService.setResponse(badResponse);
        badResponse.setReceivedData(null);
        assertThrows(DownloadingException.class, () -> countriesUpdateService.update());
    }

    @Test
    void updateDataWithEmptyStringResponse() {
        countriesUpdateService.setResponse(badResponse);
        badResponse.setReceivedData(new ByteArrayInputStream("".getBytes()));
        assertThrows(DownloadingException.class, () -> countriesUpdateService.update());
    }

    @Test
    void updateDataWithCorrectResponse() throws DownloadingException {
        countriesUpdateService.setResponse(goodResponse);
        countriesUpdateService.update();
    }
}