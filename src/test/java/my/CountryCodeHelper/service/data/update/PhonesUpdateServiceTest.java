package my.CountryCodeHelper.service.data.update;

import my.CountryCodeHelper.exception.DownloadingException;
import my.CountryCodeHelper.external.ErrorCode;
import my.CountryCodeHelper.external.ExtResponse;
import my.CountryCodeHelper.model.Country;
import my.CountryCodeHelper.model.PhoneCode;
import my.CountryCodeHelper.repo.proxy.PhoneCodeRepoProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class PhonesUpdateServiceTest {
    AutoCloseable closeable;

    @Mock
    PhoneCodeRepoProxy phoneCodeRepo;

    @InjectMocks
    PhonesUpdateService phonesUpdateService;

    PhoneCode correctPhoneCode;
    Country correctCountry;
    ExtResponse failureResponse;
    ExtResponse badResponse;
    ExtResponse goodResponse;

    @BeforeEach
    void setUp() {
        String correctCountryCode = "BE";
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
        goodResponse.setReceivedData(new ByteArrayInputStream("{\"BD\": \"880\", \"BE\": \"32\", \"BF\": \"226\"}".getBytes()));

        badResponse.setErrorCode(ErrorCode.ERROR_CODE_OK);
        badResponse.setReceivedData(new ByteArrayInputStream("some incorrect string".getBytes()));

        setMockOutput();
    }

    void setMockOutput() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        closeable.close();
    }

    @Test
    void updateDataWithFailureResponse() {
        phonesUpdateService.setResponse(failureResponse);
        assertThrows(DownloadingException.class, () -> phonesUpdateService.update());
    }

    @Test
    void updateDataWithIncorrectStringResponse() {
        phonesUpdateService.setResponse(badResponse);
        assertThrows(DownloadingException.class, () -> phonesUpdateService.update());
    }

    @Test
    void updateDataWithNullReceivedData() {
        phonesUpdateService.setResponse(badResponse);
        badResponse.setReceivedData(null);
        assertThrows(DownloadingException.class, () -> phonesUpdateService.update());
    }

    @Test
    void updateDataWithEmptyStringResponse() {
        phonesUpdateService.setResponse(badResponse);
        badResponse.setReceivedData(new ByteArrayInputStream("".getBytes()));
        assertThrows(DownloadingException.class, () -> phonesUpdateService.update());
    }

    @Test
    void updateDataWithCorrectResponse() throws DownloadingException {
        when(phoneCodeRepo.getByCountryCode(anyString())).thenReturn(correctPhoneCode);
        doNothing().when(phoneCodeRepo).save(any());
        phonesUpdateService.setResponse(goodResponse);
        Map<String, PhoneCode> map = new HashMap<>();
        map.put("BE", correctPhoneCode);
        when(phoneCodeRepo.getByCountryCodeIn(anySet())).thenReturn(map);
        phonesUpdateService.update();
    }
}